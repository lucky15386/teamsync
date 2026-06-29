<template>
  <el-card :class="{ 'card-dark': userStore.isDark }">
    <template #header>
      <div style="display:flex;justify-content:space-between;align-items:center">
        <span :style="{ fontWeight:600, color: userStore.isDark ? '#e5e7eb' : '#333' }">我的任务</span>
        <el-radio-group v-model="filter" size="small">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="TODO">待办</el-radio-button>
          <el-radio-button label="DOING">进行中</el-radio-button>
          <el-radio-button label="DONE">已完成</el-radio-button>
        </el-radio-group>
      </div>
    </template>
    <el-table :data="filteredTasks" v-loading="loading" style="width:100%" :class="{ 'el-table-dark': userStore.isDark }">
      <el-table-column prop="title" label="任务名称" show-overflow-tooltip />
      <el-table-column prop="projectName" label="所属项目" width="160" show-overflow-tooltip />
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-select v-model="row.status" size="small" @change="handleStatusChange(row)">
            <el-option label="待办" value="TODO" /><el-option label="进行中" value="DOING" /><el-option label="已完成" value="DONE" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="优先级" width="80"><template #default="{ row }"><el-tag :type="priorityType(row.priority)" size="small" effect="plain">{{ priorityLabel(row.priority) }}</el-tag></template></el-table-column>
      <el-table-column prop="deadline" label="截止日期" width="120" />
      <el-table-column label="创建时间" width="170"><template #default="{ row }">{{ formatDateTime(row.createTime) }}</template></el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { getMyTasks, updateTaskStatus } from '../api/task'
import { formatDateTime, priorityLabel, priorityType } from '../utils/format'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const tasks = ref([])
const loading = ref(false)
const filter = ref('')
const filteredTasks = computed(() => filter.value ? tasks.value.filter(t => t.status === filter.value) : tasks.value)
const handleStatusChange = async (task) => { try { await updateTaskStatus(task.id, task.status); ElMessage.success('状态更新成功') } catch {} }
onMounted(async () => { loading.value = true; try { const res = await getMyTasks(); tasks.value = res.data || [] } catch {} finally { loading.value = false } })
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
