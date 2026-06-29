<template>
  <el-card :class="{ 'card-dark': userStore.isDark }">
    <template #header>
      <div style="display:flex;justify-content:space-between;align-items:center">
        <div style="display:flex;align-items:center;gap:12px">
          <span :style="{ fontWeight: 600, color: userStore.isDark ? '#e5e7eb' : '#333' }">文档管理</span>
          <el-select v-model="categoryFilter" placeholder="全部分类" clearable style="width:140px" size="small">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </div>
        <el-button type="primary" @click="uploadDialogVisible = true"><el-icon><Upload /></el-icon>上传文档</el-button>
      </div>
    </template>
    <el-table :data="filteredDocuments" v-loading="loading" style="width:100%" :class="{ 'el-table-dark': userStore.isDark }">
      <el-table-column prop="name" label="文件名" show-overflow-tooltip />
      <el-table-column prop="category" label="分类" width="110" />
      <el-table-column label="大小" width="100"><template #default="{ row }">{{ formatFileSize(row.fileSize) }}</template></el-table-column>
      <el-table-column prop="fileType" label="类型" width="80" />
      <el-table-column prop="downloadCount" label="下载次数" width="100" />
      <el-table-column label="上传时间" width="170"><template #default="{ row }">{{ formatDateTime(row.createTime) }}</template></el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" link @click="previewDoc(row)">预览</el-button>
          <el-button size="small" type="primary" link @click="downloadDoc(row.id)">下载</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)"><template #reference><el-button size="small" type="danger" link>删除</el-button></template></el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="uploadDialogVisible" title="上传文档" width="480px">
      <el-form label-width="80px">
        <el-form-item label="所属项目">
          <el-select v-model="uploadForm.projectId" filterable placeholder="选择项目" style="width:100%">
            <el-option v-for="p in projects" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="文档分类">
          <el-select v-model="uploadForm.category" style="width:100%">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择文件">
          <el-upload :auto-upload="false" :limit="1" :on-change="handleFileChange" :on-remove="() => uploadForm.file = null">
            <el-button type="primary" plain>选择文件</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="uploading" @click="handleUploadSubmit">上传</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="previewDialogVisible" :title="previewDocName" width="80%" top="5vh">
      <div v-if="isImage" style="display: flex; justify-content: center; max-height: 70vh; overflow: auto;">
        <img :src="previewUrl" :alt="preview" style="max-width: 100%; max-height: 70vh;" />
      </div>
      <div v-else-if="isPDF" style="height: 70vh; width: 100%;">
        <iframe :src="previewUrl" style="width: 100%; height: 100%;" frameborder="0"></iframe>
      </div>
      <div v-else style="text-align: center; padding: 40px;">
        <el-icon :size="80" color="#909399"><Document /></el-icon>
        <p :style="{ color: userStore.isDark ? '#9ca3af' : '#909399', marginTop: '20px' }">该文件类型暂不支持在线预览，请点击下载查看</p>
      </div>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { getDocuments, uploadDocument, deleteDocument, downloadDocument } from '../api/document'
import { getMyProjects } from '../api/project'
import { formatDateTime, formatFileSize } from '../utils/format'
import { ElMessage } from 'element-plus'
import { Upload, Document } from '@element-plus/icons-vue'

const userStore = useUserStore()
const documents = ref([])
const projects = ref([])
const loading = ref(false)
const uploading = ref(false)
const uploadDialogVisible = ref(false)
const previewDialogVisible = ref(false)
const categoryFilter = ref('')
const categories = ['需求文档', '设计文档', '测试文档', '其他']
const uploadForm = reactive({ projectId: null, category: '其他', file: null })
const previewUrl = ref('')
const previewDocName = ref('')
const filteredDocuments = computed(() => categoryFilter.value ? documents.value.filter(d => d.category === categoryFilter.value) : documents.value)
const isImage = computed(() => {
  const ext = previewDocName.value.split('.').pop().toLowerCase()
  return ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'svg'].includes(ext)
})
const isPDF = computed(() => {
  const ext = previewDocName.value.split('.').pop().toLowerCase()
  return ext === 'pdf'
})

const fetchData = async () => { loading.value = true; try { const res = await getDocuments(); documents.value = res.data || [] } catch {} finally { loading.value = false } }
const fetchProjects = async () => { try { const res = await getMyProjects(); projects.value = res.data || [] } catch {} }
const handleFileChange = (file) => { uploadForm.file = file.raw }
const handleUploadSubmit = async () => {
  if (!uploadForm.projectId) return ElMessage.warning('请选择项目')
  if (!uploadForm.file) return ElMessage.warning('请选择文件')
  uploading.value = true
  try {
    await uploadDocument(uploadForm.projectId, uploadForm.file, uploadForm.category)
    ElMessage.success('上传成功')
    uploadDialogVisible.value = false
    uploadForm.file = null
    fetchData()
  } catch (e) {
    console.error('上传失败:', e)
    ElMessage.error(e.message || '上传失败，请检查文件大小或网络连接')
  } finally { uploading.value = false }
}
const downloadDoc = (id) => downloadDocument(id)
const previewDoc = (row) => {
  previewDocName.value = row.name
  previewUrl.value = `/api/documents/${row.id}/download`
  previewDialogVisible.value = true
}
const handleDelete = async (id) => { try { await deleteDocument(id); ElMessage.success('删除成功'); fetchData() } catch {} }
onMounted(() => { fetchData(); fetchProjects() })
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
