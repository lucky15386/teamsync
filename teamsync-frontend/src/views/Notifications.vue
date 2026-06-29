<template>
  <el-card :class="{ 'card-dark': userStore.isDark }">
    <template #header>
      <div style="display:flex;justify-content:space-between;align-items:center">
        <div style="display:flex;align-items:center;gap:12px">
          <span :style="{ fontWeight:600, color: userStore.isDark ? '#e5e7eb' : '#333' }">消息通知</span>
          <el-tag v-if="unreadCount" type="danger" size="small">{{ unreadCount }} 条未读</el-tag>
        </div>
        <el-button size="small" @click="handleMarkAllRead">全部已读</el-button>
      </div>
      </template>
    <el-table :data="notifications" v-loading="loading" style="width:100%" @row-click="handleRowClick" :class="{ 'el-table-dark': userStore.isDark }">
      <el-table-column label="" width="8"><template #default="{ row }"><div v-if="!row.isRead" style="width:8px;height:8px;border-radius:50%;background:#f56c6c"></div></template></el-table-column>
      <el-table-column prop="title" label="标题" width="200" />
      <el-table-column prop="content" label="内容" show-overflow-tooltip />
      <el-table-column label="类型" width="100"><template #default="{ row }"><el-tag :type="typeMap[row.type]?.type||'info'" size="small">{{ typeMap[row.type]?.label||row.type }}</el-tag></template></el-table-column>
      <el-table-column label="时间" width="170"><template #default="{ row }">{{ formatDateTime(row.createTime) }}</template></el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button v-if="!row.isRead" size="small" type="primary" link @click.stop="handleRead(row.id)">标为已读</el-button>
          <span v-else style="color:#67c23a;font-size:12px">已读</span>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getNotifications, markAsRead, markAllAsRead } from '../api/notification'
import { formatDateTime } from '../utils/format'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const notifications = ref([])
const loading = ref(false)
const typeMap = { TASK: { type: 'warning', label: '任务' }, PROJECT: { type: '', label: '项目' }, SYSTEM: { type: 'info', label: '系统' } }
const unreadCount = computed(() => notifications.value.filter(n => !n.isRead).length)

const fetchData = async () => { loading.value = true; try { const res = await getNotifications(); notifications.value = res.data || [] } catch {} finally { loading.value = false } }
const handleRead = async (id) => { try { await markAsRead(id); fetchData() } catch {} }
const handleMarkAllRead = async () => { try { await markAllAsRead(); ElMessage.success('全部已读'); fetchData() } catch {} }
const handleRowClick = async (row) => {
  if (!row.isRead) await handleRead(row.id)
  if (row.type === 'PROJECT' && row.relatedId) router.push(`/projects/${row.relatedId}`)
  else if (row.type === 'TASK') router.push('/tasks')
}
onMounted(fetchData)
</script>

<style scoped>
:deep(.el-table__row) { cursor: pointer; }
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
