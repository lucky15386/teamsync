<template>
  <div v-loading="loading">
    <el-card style="margin-bottom:20px">
      <div style="display:flex;justify-content:space-between;align-items:flex-start">
        <div>
          <h2 style="margin:0 0 8px">{{ project.name }}</h2>
          <p style="color:#999;margin:0 0 12px">{{ project.description || '暂无描述' }}</p>
          <div style="display:flex;gap:16px;font-size:13px;color:#666">
            <span>成员 {{ members.length }} 人</span>
            <span>任务 {{ tasks.length }} 项</span>
            <span>文档 {{ documents.length }} 个</span>
          </div>
        </div>
        <el-tag :type="projectStatusType(project.status)" size="large">{{ projectStatusLabel(project.status) }}</el-tag>
      </div>
    </el-card>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="任务看板" name="tasks">
        <div style="margin-bottom:16px;display:flex;justify-content:space-between;align-items:center">
          <el-button type="primary" @click="showTaskDialog()"><el-icon><Plus /></el-icon>新建任务</el-button>
          <span style="font-size:13px;color:#999">拖拽任务卡片可切换状态</span>
        </div>
        <el-row :gutter="16">
          <el-col :span="8" v-for="col in columns" :key="col.key">
            <div class="kanban-col" @dragover.prevent @drop="handleDrop(col.key)">
              <div class="kanban-header" :style="{ borderColor: col.color }">
                <span>{{ col.label }}</span>
                <el-tag size="small" :color="col.color" style="color:#fff">{{ col.tasks.length }}</el-tag>
              </div>
              <div class="kanban-body">
                <div
                  v-for="task in col.tasks"
                  :key="task.id"
                  class="kanban-card"
                  draggable="true"
                  @dragstart="handleDragStart(task)"
                  @click="openTaskDrawer(task)"
                >
                  <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px">
                    <span style="font-weight:500;font-size:14px">{{ task.title }}</span>
                    <el-tag :type="priorityType(task.priority)" size="small" effect="plain">{{ priorityLabel(task.priority) }}</el-tag>
                  </div>
                  <p v-if="task.description" class="task-desc">{{ task.description }}</p>
                  <div style="display:flex;justify-content:space-between;font-size:12px;color:#999">
                    <span>{{ task.assigneeName || '未分配' }}</span>
                    <span>{{ task.deadline || '无截止日期' }}</span>
                  </div>
                </div>
                <div v-if="!col.tasks.length" style="text-align:center;color:#ccc;padding:40px 0">暂无任务</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="项目成员" name="members">
        <div style="margin-bottom:16px"><el-button v-if="hasManagePermission" type="primary" @click="showMemberDialog"><el-icon><Plus /></el-icon>添加成员</el-button></div>
        <el-table :data="members" style="width:100%">
          <el-table-column label="姓名"><template #default="{ row }">{{ row.realName || row.username }}</template></el-table-column>
          <el-table-column prop="username" label="用户名" />
          <el-table-column label="角色" width="120">
            <template #default="{ row }"><el-tag :type="row.role==='OWNER'?'danger':row.role==='ADMIN'?'warning':''" size="small">{{ {OWNER:'所有者',ADMIN:'管理员',MEMBER:'成员'}[row.role] || row.role }}</el-tag></template>
          </el-table-column>
          <el-table-column label="加入时间" width="170"><template #default="{ row }">{{ formatDateTime(row.joinTime) }}</template></el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-popconfirm v-if="hasManagePermission && row.role!=='OWNER'" title="确定移除？" @confirm="handleRemoveMember(row.userId)">
                <template #reference><el-button size="small" type="danger">移除</el-button></template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="项目文档" name="docs">
        <div style="margin-bottom:16px"><el-button type="primary" @click="docUploadVisible = true"><el-icon><Upload /></el-icon>上传文档</el-button></div>
        <el-table :data="documents" style="width:100%">
          <el-table-column prop="name" label="文件名" show-overflow-tooltip />
          <el-table-column prop="category" label="分类" width="110" />
          <el-table-column label="大小" width="100"><template #default="{ row }">{{ formatFileSize(row.fileSize) }}</template></el-table-column>
          <el-table-column label="上传时间" width="170"><template #default="{ row }">{{ formatDateTime(row.createTime) }}</template></el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <el-button size="small" type="primary" link @click="downloadDoc(row.id)">下载</el-button>
              <el-popconfirm title="确定删除？" @confirm="handleDeleteDoc(row.id)"><template #reference><el-button size="small" type="danger" link>删除</el-button></template></el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 任务编辑对话框 -->
    <el-dialog v-model="taskDialogVisible" :title="editingTaskId?'编辑任务':'新建任务'" width="520px">
      <el-form ref="taskFormRef" :model="taskForm" :rules="taskRules" label-width="80px">
        <el-form-item label="任务标题" prop="title"><el-input v-model="taskForm.title" /></el-form-item>
        <el-form-item label="任务描述"><el-input v-model="taskForm.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="负责人">
          <el-select v-model="taskForm.assigneeId" clearable filterable placeholder="选择负责人" style="width:100%">
            <el-option v-for="m in members" :key="m.userId" :label="m.realName || m.username" :value="m.userId" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级"><el-select v-model="taskForm.priority" style="width:100%"><el-option label="高" value="HIGH" /><el-option label="中" value="MED" /><el-option label="低" value="LOW" /></el-select></el-form-item>
        <el-form-item label="状态"><el-select v-model="taskForm.status" style="width:100%"><el-option label="待办" value="TODO" /><el-option label="进行中" value="DOING" /><el-option label="已完成" value="DONE" /></el-select></el-form-item>
        <el-form-item label="截止日期"><el-date-picker v-model="taskForm.deadline" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button v-if="editingTaskId" type="danger" style="float:left" @click="handleDeleteTask">删除</el-button>
        <el-button @click="taskDialogVisible=false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleTaskSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 任务详情抽屉（含评论） -->
    <el-drawer v-model="drawerVisible" :title="drawerTask?.title || '任务详情'" size="420px">
      <template v-if="drawerTask">
        <el-descriptions :column="1" border size="small" style="margin-bottom:20px">
          <el-descriptions-item label="状态"><el-tag :type="taskStatusType(drawerTask.status)" size="small">{{ taskStatusLabel(drawerTask.status) }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="优先级">{{ priorityLabel(drawerTask.priority) }}</el-descriptions-item>
          <el-descriptions-item label="负责人">{{ drawerTask.assigneeName || '未分配' }}</el-descriptions-item>
          <el-descriptions-item label="截止日期">{{ drawerTask.deadline || '-' }}</el-descriptions-item>
          <el-descriptions-item label="描述">{{ drawerTask.description || '暂无描述' }}</el-descriptions-item>
        </el-descriptions>
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px">
          <span style="font-weight:600">评论 ({{ comments.length }})</span>
          <el-button size="small" @click="showTaskDialog(drawerTask)">编辑任务</el-button>
        </div>
        <div v-loading="commentsLoading" class="comment-list">
          <div v-for="c in comments" :key="c.id" class="comment-item">
            <div style="display:flex;justify-content:space-between;margin-bottom:4px">
              <span style="font-weight:500;font-size:13px">{{ c.realName || c.username }}</span>
              <span style="font-size:12px;color:#999">{{ formatDateTime(c.createTime) }}</span>
            </div>
            <p style="margin:0;font-size:13px;color:#666">{{ c.content }}</p>
          </div>
          <el-empty v-if="!comments.length" description="暂无评论" :image-size="60" />
        </div>
        <div style="margin-top:16px;display:flex;gap:8px">
          <el-input v-model="commentContent" placeholder="输入评论..." @keyup.enter="handleAddComment" />
          <el-button type="primary" :loading="commentSubmitting" @click="handleAddComment">发送</el-button>
        </div>
      </template>
    </el-drawer>

    <!-- 添加成员 -->
    <el-dialog v-model="memberDialogVisible" title="添加成员" width="400px">
      <el-form label-width="60px">
        <el-form-item label="用户"><el-select v-model="selectedUserId" filterable placeholder="选择用户" style="width:100%"><el-option v-for="u in availableUsers" :key="u.id" :label="u.realName||u.username" :value="u.id" /></el-select></el-form-item>
        <el-form-item label="角色"><el-select v-model="selectedRole" style="width:100%"><el-option label="成员" value="MEMBER" /><el-option label="管理员" value="ADMIN" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="memberDialogVisible=false">取消</el-button><el-button type="primary" @click="handleAddMember">确定</el-button></template>
    </el-dialog>

    <!-- 文档上传 -->
    <el-dialog v-model="docUploadVisible" title="上传文档" width="440px">
      <el-form label-width="80px">
        <el-form-item label="文档分类">
          <el-select v-model="docCategory" style="width:100%">
            <el-option v-for="c in docCategories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择文件">
          <el-upload :auto-upload="false" :limit="1" :on-change="(f) => docFile = f.raw" :on-remove="() => docFile = null">
            <el-button type="primary" plain>选择文件</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="docUploadVisible = false">取消</el-button>
        <el-button type="primary" :loading="docUploading" @click="handleDocUpload">上传</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getProjectById, getProjectMembers, addProjectMember, removeProjectMember } from '../api/project'
import { getTasksByProject, createTask, updateTask, deleteTask, updateTaskStatus, getTaskComments, addTaskComment } from '../api/task'
import { getDocumentsByProject, uploadDocument, deleteDocument, downloadDocument } from '../api/document'
import { getUserOptions } from '../api/user'
import { formatDateTime, formatFileSize, priorityLabel, priorityType, projectStatusLabel, projectStatusType, taskStatusLabel, taskStatusType } from '../utils/format'
import { ElMessage } from 'element-plus'
import { Plus, Upload } from '@element-plus/icons-vue'

const route = useRoute()
const userStore = useUserStore()
const projectId = computed(() => Number(route.params.id))
const loading = ref(false)
const project = ref({})
const tasks = ref([])
const members = ref([])
const documents = ref([])
const allUsers = ref([])
const activeTab = ref('tasks')
const draggingTask = ref(null)

const currentUserRole = computed(() => {
  if (!userStore.user?.id) return null
  const member = members.value.find(m => m.userId === userStore.user.id)
  return member?.role
})
const hasManagePermission = computed(() => {
  const role = currentUserRole.value
  return role === 'OWNER' || role === 'ADMIN'
})
const isOwner = computed(() => currentUserRole.value === 'OWNER')

const columns = computed(() => [
  { key: 'TODO', label: '待办', color: '#e6a23c', tasks: tasks.value.filter(t => t.status === 'TODO') },
  { key: 'DOING', label: '进行中', color: '#409eff', tasks: tasks.value.filter(t => t.status === 'DOING') },
  { key: 'DONE', label: '已完成', color: '#67c23a', tasks: tasks.value.filter(t => t.status === 'DONE') }
])
const availableUsers = computed(() => allUsers.value.filter(u => !members.value.find(m => m.userId === u.id)))

const handleDragStart = (task) => { draggingTask.value = task }
const handleDrop = async (status) => {
  if (!draggingTask.value || draggingTask.value.status === status) return
  try {
    await updateTaskStatus(draggingTask.value.id, status)
    draggingTask.value.status = status
    ElMessage.success('任务状态已更新')
    fetchTasks()
  } catch {} finally { draggingTask.value = null }
}

const taskDialogVisible = ref(false)
const editingTaskId = ref(null)
const submitting = ref(false)
const taskFormRef = ref(null)
const taskForm = reactive({ title: '', description: '', priority: 'MED', status: 'TODO', deadline: '', assigneeId: null })
const taskRules = { title: [{ required: true, message: '请输入任务标题', trigger: 'blur' }] }

const showTaskDialog = (task) => {
  if (task) {
    editingTaskId.value = task.id
    Object.assign(taskForm, { title: task.title, description: task.description, priority: task.priority, status: task.status, deadline: task.deadline || '', assigneeId: task.assigneeId || null })
  } else {
    editingTaskId.value = null
    Object.assign(taskForm, { title: '', description: '', priority: 'MED', status: 'TODO', deadline: '', assigneeId: null })
  }
  taskDialogVisible.value = true
}

const handleTaskSubmit = async () => {
  await taskFormRef.value.validate()
  submitting.value = true
  try {
    if (editingTaskId.value) await updateTask(editingTaskId.value, taskForm)
    else await createTask({ ...taskForm, projectId: projectId.value })
    ElMessage.success(editingTaskId.value ? '更新成功' : '创建成功')
    taskDialogVisible.value = false
    fetchTasks()
  } catch {} finally { submitting.value = false }
}
const handleDeleteTask = async () => {
  try { await deleteTask(editingTaskId.value); ElMessage.success('删除成功'); taskDialogVisible.value = false; drawerVisible.value = false; fetchTasks() } catch {}
}

const drawerVisible = ref(false)
const drawerTask = ref(null)
const comments = ref([])
const commentsLoading = ref(false)
const commentContent = ref('')
const commentSubmitting = ref(false)

const openTaskDrawer = async (task) => {
  drawerTask.value = task
  drawerVisible.value = true
  commentContent.value = ''
  commentsLoading.value = true
  try { const r = await getTaskComments(task.id); comments.value = r.data || [] } catch {} finally { commentsLoading.value = false }
}
const handleAddComment = async () => {
  if (!commentContent.value.trim()) return ElMessage.warning('请输入评论内容')
  commentSubmitting.value = true
  try {
    await addTaskComment(drawerTask.value.id, { content: commentContent.value })
    commentContent.value = ''
    const r = await getTaskComments(drawerTask.value.id)
    comments.value = r.data || []
    ElMessage.success('评论成功')
  } catch {} finally { commentSubmitting.value = false }
}

const memberDialogVisible = ref(false)
const selectedUserId = ref(null)
const selectedRole = ref('MEMBER')
const showMemberDialog = () => { selectedUserId.value = null; selectedRole.value = 'MEMBER'; memberDialogVisible.value = true }
const handleAddMember = async () => {
  if (!selectedUserId.value) return ElMessage.warning('请选择用户')
  try { await addProjectMember(projectId.value, { userId: selectedUserId.value, role: selectedRole.value }); ElMessage.success('添加成功'); memberDialogVisible.value = false; fetchMembers() } catch {}
}
const handleRemoveMember = async (userId) => { try { await removeProjectMember(projectId.value, userId); ElMessage.success('移除成功'); fetchMembers() } catch {} }

const docUploadVisible = ref(false)
const docUploading = ref(false)
const docCategory = ref('其他')
const docFile = ref(null)
const docCategories = ['需求文档', '设计文档', '测试文档', '其他']
const handleDocUpload = async () => {
  if (!docFile.value) return ElMessage.warning('请选择文件')
  docUploading.value = true
  try { await uploadDocument(projectId.value, docFile.value, docCategory.value); ElMessage.success('上传成功'); docUploadVisible.value = false; docFile.value = null; fetchDocs() } catch {} finally { docUploading.value = false }
}
const downloadDoc = (id) => downloadDocument(id)
const handleDeleteDoc = async (id) => { try { await deleteDocument(id); ElMessage.success('删除成功'); fetchDocs() } catch {} }

const fetchTasks = async () => { try { const r = await getTasksByProject(projectId.value); tasks.value = r.data || [] } catch {} }
const fetchMembers = async () => { try { const r = await getProjectMembers(projectId.value); members.value = r.data || [] } catch {} }
const fetchDocs = async () => { try { const r = await getDocumentsByProject(projectId.value); documents.value = r.data || [] } catch {} }

onMounted(async () => {
  loading.value = true
  try {
    const [proj] = await Promise.all([getProjectById(projectId.value), fetchTasks(), fetchMembers(), fetchDocs()])
    project.value = proj.data || {}
  } catch {} finally { loading.value = false }
  try { const r = await getUserOptions(); allUsers.value = r.data || [] } catch {}
})
</script>

<style scoped>
.kanban-col { background:#f5f7fa; border-radius:8px; min-height:420px; }
.kanban-header { padding:12px 16px; font-weight:600; font-size:15px; border-top:3px solid; border-radius:8px 8px 0 0; display:flex; justify-content:space-between; align-items:center; background:#fff; }
.kanban-body { padding:8px; min-height:340px; }
.kanban-card { background:#fff; border-radius:8px; padding:12px; margin-bottom:8px; cursor:grab; box-shadow:0 1px 4px rgba(0,0,0,0.08); transition:box-shadow 0.2s; }
.kanban-card:hover { box-shadow:0 4px 12px rgba(0,0,0,0.15); }
.kanban-card:active { cursor:grabbing; }
.task-desc { margin:0 0 8px; font-size:12px; color:#888; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.comment-list { max-height:320px; overflow-y:auto; }
.comment-item { padding:10px 0; border-bottom:1px solid #f0f0f0; }
.comment-item:last-child { border-bottom:none; }
</style>
