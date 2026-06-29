<template>
  <el-row :gutter="20">
    <el-col :span="8">
      <el-card>
        <div style="text-align:center;padding:20px 0">
          <el-avatar :size="80">{{ userStore.user?.realName?.charAt(0) || 'U' }}</el-avatar>
          <h3 style="margin:12px 0 4px">{{ userStore.user?.realName || userStore.user?.username }}</h3>
          <p style="color:#999;margin:0">@{{ userStore.user?.username }}</p>
          <el-tag style="margin-top:8px" :type="roleTagType(userStore.user?.role)">{{ roleLabel(userStore.user?.role) }}</el-tag>
        </div>
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="邮箱">{{ userStore.user?.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机">{{ userStore.user?.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ formatDateTime(userStore.user?.createTime) }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
    </el-col>
    <el-col :span="16">
      <el-card style="margin-bottom:20px">
        <template #header><span style="font-weight:600">编辑资料</span></template>
        <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="80px" style="max-width:500px">
          <el-form-item label="姓名" prop="realName"><el-input v-model="profileForm.realName" /></el-form-item>
          <el-form-item label="邮箱"><el-input v-model="profileForm.email" /></el-form-item>
          <el-form-item label="手机"><el-input v-model="profileForm.phone" /></el-form-item>
          <el-form-item><el-button type="primary" :loading="saving" @click="handleSaveProfile">保存修改</el-button></el-form-item>
        </el-form>
      </el-card>
      <el-card>
        <template #header><span style="font-weight:600">修改密码</span></template>
        <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="80px" style="max-width:500px">
          <el-form-item label="旧密码" prop="oldPassword"><el-input v-model="pwdForm.oldPassword" type="password" show-password /></el-form-item>
          <el-form-item label="新密码" prop="newPassword"><el-input v-model="pwdForm.newPassword" type="password" show-password /></el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword"><el-input v-model="pwdForm.confirmPassword" type="password" show-password /></el-form-item>
          <el-form-item><el-button type="primary" :loading="changingPwd" @click="handleChangePassword">修改密码</el-button></el-form-item>
        </el-form>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { updateUser, changePassword } from '../api/user'
import { formatDateTime, roleLabel, roleTagType } from '../utils/format'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const profileFormRef = ref(null)
const pwdFormRef = ref(null)
const saving = ref(false)
const changingPwd = ref(false)

const profileForm = reactive({ realName: '', email: '', phone: '' })
const profileRules = { realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }] }
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const validateConfirm = (r, v, cb) => { if (v !== pwdForm.newPassword) cb(new Error('两次密码不一致')); else cb() }
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '至少6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }, { validator: validateConfirm, trigger: 'blur' }]
}

onMounted(async () => {
  if (userStore.user) {
    profileForm.realName = userStore.user.realName || ''
    profileForm.email = userStore.user.email || ''
    profileForm.phone = userStore.user.phone || ''
  }
})

const handleSaveProfile = async () => {
  await profileFormRef.value.validate()
  saving.value = true
  try {
    await updateUser(userStore.user.id, { realName: profileForm.realName, email: profileForm.email, phone: profileForm.phone })
    await userStore.fetchUser()
    ElMessage.success('保存成功')
  } catch {} finally { saving.value = false }
}

const handleChangePassword = async () => {
  await pwdFormRef.value.validate()
  changingPwd.value = true
  try {
    await changePassword(userStore.user.id, { oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
    ElMessage.success('密码修改成功')
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
  } catch {} finally { changingPwd.value = false }
}
</script>
