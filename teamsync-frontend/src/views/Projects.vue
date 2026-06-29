<template>
  <el-card :class="{ 'card-dark': userStore.isDark }">
    <template #header>
      <div style="display:flex;justify-content:space-between;align-items:center">
        <div style="display:flex;align-items:center;gap:12px">
          <span :style="{ fontWeight:600, color: userStore.isDark ? '#e5e7eb' : '#333' }">项目列表</span>
          <el-radio-group v-model="statusFilter" size="small">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="ACTIVE">进行中</el-radio-button>
            <el-radio-button label="COMPLETED">已完成</el-radio-button>
            <el-radio-button label="ARCHIVED">已归档</el-radio-button>
          </el-radio-group>
        </div>
        <el-button type="primary" @click="showDialog()"><el-icon><Plus /></el-icon>新建项目</el-button>
      </div>
    </template>
    <el-table :data="filteredProjects" v-loading="loading" style="width:100%" :class="{ 'el-table-dark': userStore.isDark }">
      <el-table-column prop="name" label="项目名称" show-overflow-tooltip>
        <template #default="{ row }"><el-link type="primary" @click="$router.push(`/projects/${row.id}`)">{{ row.name }}</el-link></template>
      </el-table-column>
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template #default="{ row }"><el-tag :type="projectStatusType(row.status)" size="small">{{ projectStatusLabel(row.status) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="创建时间" width="170"><template #default="{ row }">{{ formatDateTime(row.createTime) }}</template></el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showDialog(row)">编辑</el-button>
          <el-popconfirm title="确定删除？删除后关联任务与文档将一并移除" @confirm="handleDelete(row.id)">
            <template #reference><el-button size="small" type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" :title="editingId?'编辑项目':'新建项目'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="项目名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="项目描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item v-if="editingId" label="项目状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="进行中" value="ACTIVE" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已归档" value="ARCHIVED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { getMyProjects, createProject, updateProject, deleteProject } from '../api/project'
import { formatDateTime, projectStatusLabel, projectStatusType } from '../utils/format'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const userStore = useUserStore()
const projects = ref([])
const loading = ref(false)
const statusFilter = ref('')
const dialogVisible = ref(false)
const submitting = ref(false)
const editingId = ref(null)
const formRef = ref(null)
const form = reactive({ name: '', description: '', status: 'ACTIVE' })
const rules = { name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }] }
const filteredProjects = computed(() => statusFilter.value ? projects.value.filter(p => p.status === statusFilter.value) : projects.value)

const fetchData = async () => { loading.value = true; try { const res = await getMyProjects(); projects.value = res.data || [] } catch {} finally { loading.value = false } }
const showDialog = (row) => {
  if (row) { editingId.value = row.id; form.name = row.name; form.description = row.description; form.status = row.status }
  else { editingId.value = null; form.name = ''; form.description = ''; form.status = 'ACTIVE' }
  dialogVisible.value = true
}
const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (editingId.value) await updateProject(editingId.value, { name: form.name, description: form.description, status: form.status })
    else await createProject({ name: form.name, description: form.description })
    ElMessage.success(editingId.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    fetchData()
  } catch {} finally { submitting.value = false }
}
const handleDelete = async (id) => { try { await deleteProject(id); ElMessage.success('删除成功'); fetchData() } catch {} }
onMounted(fetchData)
</script>

<style scoped>
.card-dark :deep(.el-card) {
  background-color: #1f2937 !important;
  border-color: #374151 !important;
}
.card-dark :deep(.el-card__header) {
  background-color: #1f2937 !important;
  border-bottom-color: #374151 !important;
}
.card-dark :deep(.el-card__body) {
  background-color: #1f2937 !important;
}
.el-table-dark :deep(.el-table) {
  background-color: transparent !important;
}
.el-table-dark :deep(.el-table__row) {
  background-color: transparent !important;
}
.el-table-dark :deep(.el-table__header-wrapper th) {
  background-color: #1f2937 !important;
  color: #e5e7eb !important;
}
.el-table-dark :deep(.el-table__body-wrapper td) {
  background-color: transparent !important;
  color: #e5e7eb !important;
  border-bottom-color: #374151 !important;
}
</style>
