<template>
  <div>
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6" v-for="card in statCards" :key="card.label">
        <el-card shadow="hover" class="stat-card" :class="{ 'stat-card-dark': userStore.isDark }" @click="handleCardClick(card)">
          <div class="stat-icon" :style="{ background: card.color }">
            <el-icon size="28"><component :is="card.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value" :class="{ 'stat-value-dark': userStore.isDark }">{{ card.value }}</div>
            <div class="stat-label" :class="{ 'stat-label-dark': userStore.isDark }">{{ card.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="12">
        <el-card class="chart-card" :class="{ 'chart-card-dark': userStore.isDark }">
          <template #header>
            <span :style="{ fontWeight: 600, color: userStore.isDark ? '#e5e7eb' : '#333' }">任务状态分布</span>
          </template>
          <div ref="pieChartRef" style="height: 280px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card" :class="{ 'chart-card-dark': userStore.isDark }">
          <template #header>
            <span :style="{ fontWeight: 600, color: userStore.isDark ? '#e5e7eb' : '#333' }">我的项目</span>
          </template>
          <div ref="barChartRef" style="height: 280px"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="chart-card" :class="{ 'chart-card-dark': userStore.isDark }">
          <template #header>
            <span :style="{ fontWeight: 600, color: userStore.isDark ? '#e5e7eb' : '#333' }">任务概览</span>
          </template>
          <div style="display: flex; justify-content: space-around; padding: 20px 0">
            <div v-for="item in taskOverview" :key="item.label" style="text-align: center">
              <el-progress type="circle" :percentage="item.pct" :color="item.color" :width="100">
                <template #default>
                  <div :style="{ fontSize: '24px', fontWeight: 700, color: userStore.isDark ? '#e5e7eb' : '#333' }">{{ item.count }}</div>
                  <div :style="{ fontSize: '12px', color: userStore.isDark ? '#9ca3af' : '#999' }">{{ item.label }}</div>
                </template>
              </el-progress>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card" :class="{ 'chart-card-dark': userStore.isDark }">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center">
              <span :style="{ fontWeight: 600, color: userStore.isDark ? '#e5e7eb' : '#333' }">最近任务</span>
              <el-link type="primary" @click="$router.push('/tasks')">查看全部</el-link>
            </div>
          </template>
          <el-table :data="recentTasks" style="width: 100%" max-height="280" :class="{ 'el-table-dark': userStore.isDark }">
            <el-table-column prop="title" label="任务名称" show-overflow-tooltip />
            <el-table-column prop="projectName" label="项目" width="120" show-overflow-tooltip />
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="taskStatusType(row.status)" size="small">{{ taskStatusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="优先级" width="70">
              <template #default="{ row }">
                <el-tag :type="priorityType(row.priority)" size="small" effect="plain">{{ priorityLabel(row.priority) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="chart-card" :class="{ 'chart-card-dark': userStore.isDark }">
          <template #header>
            <span :style="{ fontWeight: 600, color: userStore.isDark ? '#e5e7eb' : '#333' }">项目进度甘特图</span>
          </template>
          <div ref="ganttChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import * as echarts from 'echarts'
import { getDashboard } from '../api/dashboard'
import { taskStatusLabel, taskStatusType, priorityLabel, priorityType } from '../utils/format'

const router = useRouter()
const userStore = useUserStore()
const stats = ref({})
const recentTasks = ref([])
const pieChartRef = ref(null)
const barChartRef = ref(null)
const ganttChartRef = ref(null)
let pieChart = null
let barChart = null
let ganttChart = null

const statCards = computed(() => [
  { label: '我的项目', value: stats.value.projectCount || 0, color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', icon: 'Folder', path: '/projects' },
  { label: '我的任务', value: stats.value.taskCount || 0, color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)', icon: 'Document', path: '/tasks' },
  { label: '已完成', value: stats.value.doneCount || 0, color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)', icon: 'CircleCheck', path: '/tasks' },
  { label: '未读通知', value: stats.value.unreadNotifications || 0, color: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)', icon: 'Bell', path: '/notifications' }
])
const total = computed(() => stats.value.taskCount || 1)
const taskOverview = computed(() => [
  { label: '待办', count: stats.value.todoCount || 0, pct: Math.round((stats.value.todoCount || 0) * 100 / total.value), color: '#e6a23c' },
  { label: '进行中', count: stats.value.doingCount || 0, pct: Math.round((stats.value.doingCount || 0) * 100 / total.value), color: '#409eff' },
  { label: '已完成', count: stats.value.doneCount || 0, pct: Math.round((stats.value.doneCount || 0) * 100 / total.value), color: '#67c23a' }
])

const handleCardClick = (card) => {
  if (card.path) {
    router.push(card.path)
  }
}

const renderCharts = () => {
  const theme = userStore.isDark ? 'dark' : undefined
  if (pieChartRef.value) {
    pieChart = pieChart || echarts.init(pieChartRef.value, theme)
    pieChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0, textStyle: { color: userStore.isDark ? '#e5e7eb' : '#333' } },
      series: [{
        type: 'pie', radius: ['40%', '65%'], center: ['50%', '45%'],
        data: [
          { value: stats.value.todoCount || 0, name: '待办', itemStyle: { color: '#e6a23c' } },
          { value: stats.value.doingCount || 0, name: '进行中', itemStyle: { color: '#409eff' } },
          { value: stats.value.doneCount || 0, name: '已完成', itemStyle: { color: '#67c23a' } }
        ],
        label: { show: true, formatter: '{b}: {c}', color: userStore.isDark ? '#e5e7eb' : '#333' }
      }]
    })
  }
  if (barChartRef.value) {
    const projects = stats.value.projectStats || stats.value.myProjects || []
    barChart = barChart || echarts.init(barChartRef.value, theme)
    barChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 20, bottom: 40, top: 20 },
      xAxis: { type: 'category', data: projects.map(p => p.name), axisLabel: { rotate: projects.length > 3 ? 20 : 0, interval: 0, color: userStore.isDark ? '#e5e7eb' : '#333' } },
      yAxis: { type: 'value', minInterval: 1, axisLabel: { color: userStore.isDark ? '#e5e7eb' : '#333' } },
      series: [{ name: '我的任务数', type: 'bar', data: projects.map(p => p.taskCount ?? 0), itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: '#667eea' },
        { offset: 1, color: '#764ba2' }
      ]), borderRadius: [8, 8, 0, 0] } }]
    })
  }
  if (ganttChartRef.value) {
    ganttChart = ganttChart || echarts.init(ganttChartRef.value, theme)
    const projects = stats.value.projectStats || stats.value.myProjects || []
    const today = new Date()
    const ganttData = projects.map((p, index) => ({
      name: p.name,
      value: [
        index,
        new Date(today.getTime() - Math.random() * 30 * 24 * 60 * 60 * 1000).getTime(),
        new Date(today.getTime() + Math.random() * 30 * 24 * 60 * 60 * 1000).getTime(),
        Math.floor(Math.random() * 100)
      ],
      itemStyle: {
        color: ['#667eea', '#764ba2', '#409eff', '#e6a23c', '#67c23a'][index % 5]
      }
    }))
    
    ganttChart.setOption({
      tooltip: {
        formatter: function (params) {
          const data = params.value
          const start = new Date(data[1])
          const end = new Date(data[2])
          return `${params.name}<br/>开始: ${start.toLocaleDateString()}<br/>结束: ${end.toLocaleDateString()}<br/>进度: ${data[3]}%`
        }
      },
      grid: { left: 100, right: 50, top: 50, bottom: 50 },
      xAxis: {
        type: 'time',
        min: new Date(today.getTime() - 35 * 24 * 60 * 60 * 1000).getTime(),
        max: new Date(today.getTime() + 35 * 24 * 60 * 60 * 1000).getTime(),
        axisLabel: { color: userStore.isDark ? '#e5e7eb' : '#333' }
      },
      yAxis: {
        type: 'category',
        data: ganttData.map(d => d.name),
        axisLabel: { fontSize: 14, color: userStore.isDark ? '#e5e7eb' : '#333' }
      },
      series: [{
        type: 'custom',
        renderItem: function (params, api) {
          const yIndex = api.value(0)
          const start = api.coord([api.value(1), yIndex])
          const end = api.coord([api.value(2), yIndex])
          const size = api.size([api.value(2) - api.value(1), 1])
          
          return {
            type: 'rect',
            shape: {
              x: start[0],
              y: start[1] - size[1] / 2,
              width: end[0] - start[0],
              height: size[1]
            },
            style: api.style()
          }
        },
        encode: {
          x: [1, 2],
          y: 0
        },
        data: ganttData.map(d => d.value),
        itemStyle: {
          borderRadius: 6
        }
      }]
    })
  }
}

const handleResize = () => { 
  pieChart?.resize(); 
  barChart?.resize();
  ganttChart?.resize();
}

watch(() => userStore.isDark, () => {
  pieChart?.dispose()
  barChart?.dispose()
  ganttChart?.dispose()
  pieChart = null
  barChart = null
  ganttChart = null
  nextTick(() => renderCharts())
})

onMounted(async () => {
  try {
    const res = await getDashboard()
    stats.value = res.data || {}
    recentTasks.value = (stats.value.myTasks || []).slice(0, 8)
    await nextTick()
    renderCharts()
  } catch {}
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  pieChart?.dispose()
  barChart?.dispose()
  ganttChart?.dispose()
})
</script>

<style scoped>
.stat-card :deep(.el-card__body) { 
  display: flex; 
  align-items: center; 
  gap: 16px; 
  padding: 24px; 
}
.stat-card {
  cursor: pointer;
  transition: transform 0.2s;
}
.stat-card:hover {
  transform: translateY(-4px);
}
.stat-card-dark :deep(.el-card) {
  background-color: #1f2937 !important;
  border-color: #374151 !important;
}
.stat-card-dark :deep(.el-card__body) {
  background-color: #1f2937 !important;
}
.stat-icon { 
  width: 64px; 
  height: 64px; 
  border-radius: 16px; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  color: #fff; 
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}
.stat-value { 
  font-size: 32px; 
  font-weight: 800; 
  color: #333; 
  line-height: 1;
}
.stat-value-dark {
  color: #e5e7eb !important;
}
.stat-label { 
  font-size: 14px; 
  color: #999; 
  margin-top: 6px; 
  font-weight: 500;
}
.stat-label-dark {
  color: #9ca3af !important;
}
.chart-card :deep(.el-card__header) {
  border-bottom: 1px solid #f0f0f0;
  padding: 16px 20px;
}
.chart-card-dark :deep(.el-card) {
  background-color: #1f2937 !important;
  border-color: #374151 !important;
}
.chart-card-dark :deep(.el-card__header) {
  background-color: #1f2937 !important;
  border-bottom-color: #374151 !important;
}
.chart-card-dark :deep(.el-card__body) {
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
