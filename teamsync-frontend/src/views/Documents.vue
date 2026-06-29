<template>
  <div class="documents-container">
    <el-card class="box-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="title-text">项目文档管理</span>
          <div style="display:flex;gap:12px;align-items:center">
            <el-select v-model="selectedProject" placeholder="选择项目" style="width:200px" clearable>
              <el-option v-for="p in projects" :key="p.id" :label="p.name" :value="p.id" />
            </el-select>
            <el-upload
              class="upload-btn"
              action="#"
              :show-file-list="false"
              :http-request="handleCustomUpload"
              :before-upload="beforeUpload"
            >
              <el-button type="primary" :disabled="!selectedProject">上传新文档</el-button>
            </el-upload>
          </div>
        </div>
      </template>
      
      <el-table :data="documentList" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="文档名称" min-width="220">
          <template #default="{ row }">
            <div style="display: flex; align-items: center;">
              <el-icon style="margin-right: 8px; color: var(--el-text-color-secondary);"><Document /></el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="fileSize" label="大小" width="120">
          <template #default="{ row }">
            {{ formatFileSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="uploaderName" label="上传者" width="150" />
        <el-table-column prop="createTime" label="上传时间" width="180" />
        
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="previewDoc(row)">预览</el-button>
            <el-button link type="success" @click="downloadDoc(row)">下载</el-button>
            <el-button link type="danger" @click="deleteDoc(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 预览对话框 -->
    <el-dialog v-model="previewDialogVisible" :title="previewFileName" width="80%" top="5vh">
      <div class="preview-container">
        <div v-if="previewLoading" class="loading-wrapper">
          <el-icon class="is-loading" :size="40"><Loading /></el-icon>
          <span style="margin-left:12px">加载中...</span>
        </div>
        <template v-else-if="previewError">
          <el-alert type="error" :title="previewError" show-icon />
        </template>
        <template v-else-if="previewType === 'image'">
          <img :src="previewUrl" style="max-width:100%;display:block;margin:0 auto" alt="预览" />
        </template>
        <template v-else-if="previewType === 'pdf'">
          <iframe :src="previewUrl" style="width:100%;height:600px;border:none" title="PDF预览" />
        </template>
        <template v-else-if="previewType === 'text'">
          <div class="text-preview">
            <pre>{{ previewTextContent }}</pre>
          </div>
        </template>
        <template v-else>
          <div class="unsupported-preview">
            <el-alert type="info" title="该文件类型不支持在线预览" description="请点击下载按钮查看文件" show-icon />
          </div>
        </template>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, Loading } from '@element-plus/icons-vue'
import { getDocuments, uploadDocument, deleteDocument, downloadDocument, previewDocument } from '../api/document'
import { getMyProjects } from '../api/project'

const loading = ref(false)
const documentList = ref([])
const projects = ref([])
const selectedProject = ref(null)

// 预览相关
const previewDialogVisible = ref(false)
const previewLoading = ref(false)
const previewError = ref('')
const previewUrl = ref('')
const previewType = ref('')
const previewFileName = ref('')
const previewTextContent = ref('')

const isPreviewable = (fileName, fileType) => {
  const previewableExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp', '.pdf', '.txt', '.md', '.csv', '.json']
  const lowerName = fileName.toLowerCase()
  for (const ext of previewableExtensions) {
    if (lowerName.endsWith(ext)) {
      return true
    }
  }
  const previewableMimeTypes = ['image/', 'application/pdf', 'text/']
  if (fileType) {
    for (const mime of previewableMimeTypes) {
      if (fileType.startsWith(mime)) {
        return true
      }
    }
  }
  return false
}

const getPreviewType = (fileName, fileType) => {
  const lowerName = fileName.toLowerCase()
  if (lowerName.endsWith('.pdf')) return 'pdf'
  if (lowerName.endsWith('.jpg') || lowerName.endsWith('.jpeg') || 
      lowerName.endsWith('.png') || lowerName.endsWith('.gif') ||
      lowerName.endsWith('.bmp') || lowerName.endsWith('.webp')) {
    return 'image'
  }
  if (fileType && fileType.startsWith('image/')) return 'image'
  if (fileType && fileType.startsWith('application/pdf')) return 'pdf'
  if (fileType && fileType.startsWith('text/')) return 'text'
  if (lowerName.endsWith('.txt') || lowerName.endsWith('.md') || 
      lowerName.endsWith('.csv') || lowerName.endsWith('.json')) {
    return 'text'
  }
  return 'unsupported'
}

const formatFileSize = (size) => {
  if (!size) return '0 B'
  const num = 1024
  if (size < num) return size + ' B'
  if (size < Math.pow(num, 2)) return (size / num).toFixed(2) + ' KB'
  if (size < Math.pow(num, 3)) return (size / Math.pow(num, 2)).toFixed(2) + ' MB'
  return (size / Math.pow(num, 3)).toFixed(2) + ' GB'
}

const fetchDocuments = async () => {
  loading.value = true
  try {
    const res = await getDocuments()
    if (res.code === 200) {
      documentList.value = res.data || []
    }
  } catch (error) {
    console.error('获取文档列表失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchProjects = async () => {
  try {
    const res = await getMyProjects()
    if (res.code === 200) {
      projects.value = res.data || []
    }
  } catch (error) {
    console.error('获取项目列表失败:', error)
  }
}

const beforeUpload = (file) => {
  const isLt50M = file.size / 1024 / 1024 < 50
  if (!isLt50M) {
    ElMessage.error('上传文件大小不能超过 50MB!')
  }
  return isLt50M
}

const handleCustomUpload = async (options) => {
  if (!selectedProject.value) {
    ElMessage.warning('请先选择项目')
    return
  }
  
  try {
    const res = await uploadDocument(selectedProject.value, options.file)
    if (res.code === 200) {
      ElMessage.success('上传成功')
      fetchDocuments()
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (error) {
    ElMessage.error('上传出错，请检查网络或权限')
  }
}

const previewDoc = async (row) => {
  previewFileName.value = row.name
  previewError.value = ''
  previewLoading.value = true
  previewDialogVisible.value = true
  
  try {
    const res = await previewDocument(row.id)
    if (res.code) {
      // 如果是错误响应
      previewError.value = res.message || '预览失败'
      previewLoading.value = false
      return
    }
    
    const blob = res
    previewType.value = getPreviewType(row.name, row.fileType)
    
    if (previewType.value === 'text') {
      const text = await blob.text()
      previewTextContent.value = text
    } else {
      const url = URL.createObjectURL(blob)
      previewUrl.value = url
    }
  } catch (error) {
    console.error('预览失败:', error)
    previewError.value = error.message || '预览失败'
  } finally {
    previewLoading.value = false
  }
}

const downloadDoc = async (row) => {
  try {
    const res = await downloadDocument(row.id)
    if (res.code) {
      ElMessage.error(res.message || '下载失败')
      return
    }
    
    const blob = res
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = row.name
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
    ElMessage.success('下载开始')
  } catch (error) {
    console.error('下载失败:', error)
    ElMessage.error('下载失败')
  }
}

const deleteDoc = (row) => {
  ElMessageBox.confirm('确定要永久删除该文档吗？', '系统警告', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteDocument(row.id)
      if (res.code === 200) {
        ElMessage.success('文档已删除')
        fetchDocuments()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  fetchDocuments()
  fetchProjects()
})
</script>

<style scoped>
.documents-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.title-text {
  font-weight: 600;
  font-size: 16px;
}
.preview-container {
  min-height: 200px;
}
.loading-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 0;
}
.text-preview {
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
}
.text-preview pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}
.unsupported-preview {
  padding: 20px 0;
}
</style>
