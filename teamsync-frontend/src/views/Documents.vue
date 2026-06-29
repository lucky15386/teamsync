<template>
  <div class="documents-container">
    <el-card class="box-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="title-text">项目文档管理</span>
          <el-upload
            class="upload-btn"
            :show-file-list="false"
            :http-request="handleCustomUpload"
            :before-upload="beforeUpload"
          >
            <el-button type="primary">上传新文档</el-button>
          </el-upload>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document } from '@element-plus/icons-vue'
import request from '../utils/request'

const loading = ref(false)
const documentList = ref([])

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
    const res = await request.get('/documents')
    if (res.code === 200) {
      documentList.value = res.data || []
    }
  } catch (error) {
    console.error('获取文档列表失败:', error)
  } finally {
    loading.value = false
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
  const formData = new FormData()
  formData.append('file', options.file)
  
  try {
    const res = await request.post('/documents/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    
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

const previewDoc = (row) => {
  if(row.fileUrl) {
    window.open(row.fileUrl, '_blank')
  } else {
    ElMessage.warning('暂无在线预览链接')
  }
}

const downloadDoc = (row) => {
  if(row.fileUrl) {
    window.open(row.fileUrl, '_blank')
  } else {
    ElMessage.warning('下载链接不可用')
  }
}

const deleteDoc = (row) => {
  ElMessageBox.confirm('确定要永久删除该文档吗？', '系统警告', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.delete(`/documents/${row.id}`)
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
</style>
