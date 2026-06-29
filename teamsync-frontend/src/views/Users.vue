<template>
  <el-card>
    <template #header><span style="font-weight:600">用户管理</span></template>
    <el-table :data="users" v-loading="loading" style="width:100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column label="姓名" width="120"><template #default="{ row }">{{ row.realName || '-' }}</template></el-table-column>
      <el-table-column prop="email" label="邮箱" show-overflow-tooltip />
      <el-table-column prop="phone" label="手机" width="130" />
      <el-table-column label="角色" width="100"><template #default="{ row }"><el-tag :type="roleTagType(row.role)" size="small">{{ roleLabel(row.role) }}</el-tag></template></el-table-column>
      <el-table-column label="状态" width="100"><template #default="{ row }"><el-switch :model-value="row.status===1" @change="(val) => handleStatusChange(row.id, val?1:0)" /></template></el-table-column>
      <el-table-column label="注册时间" width="170"><template #default="{ row }">{{ formatDateTime(row.createTime) }}</template></el-table-column>
      <el-table-column label="操作" width="80" fixed="right"><template #default="{ row }"><el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)"><template #reference><el-button size="small" type="danger" link :disabled="row.role==='ADMIN'">删除</el-button></template></el-popconfirm></template></el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserList, updateUserStatus, deleteUser } from '../api/user'
import { formatDateTime, roleLabel, roleTagType } from '../utils/format'
import { ElMessage } from 'element-plus'

const users = ref([])
const loading = ref(false)
const fetchData = async () => { loading.value = true; try { const res = await getUserList(); users.value = res.data || [] } catch {} finally { loading.value = false } }
const handleStatusChange = async (id, status) => { try { await updateUserStatus(id, status); ElMessage.success('状态更新成功'); fetchData() } catch {} }
const handleDelete = async (id) => { try { await deleteUser(id); ElMessage.success('删除成功'); fetchData() } catch {} }
onMounted(fetchData)
</script>
