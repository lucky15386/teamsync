<template>
  <el-card>
    <template #header>
      <div style="display:flex;justify-content:space-between;align-items:center">
        <span style="font-weight:600">操作日志</span>
        <el-tag type="info">最近 100 条</el-tag>
      </div>
    </template>
    <el-table :data="logs" v-loading="loading" style="width:100%">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="username" label="操作人" width="120" />
      <el-table-column prop="operation" label="操作描述" show-overflow-tooltip />
      <el-table-column prop="method" label="请求方法" width="220" show-overflow-tooltip />
      <el-table-column prop="ip" label="IP 地址" width="140" />
      <el-table-column label="操作时间" width="170"><template #default="{ row }">{{ formatDateTime(row.createTime) }}</template></el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOperationLogs } from '../api/operationLog'
import { formatDateTime } from '../utils/format'

const logs = ref([])
const loading = ref(false)
onMounted(async () => {
  loading.value = true
  try { const res = await getOperationLogs(); logs.value = res.data || [] } catch {} finally { loading.value = false }
})
</script>
