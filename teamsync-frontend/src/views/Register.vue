<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header"><el-icon size="40" color="#409eff"><Connection /></el-icon><h1>注册账号</h1><p>加入 TeamSync 团队协作平台</p></div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" size="large">
        <el-form-item prop="username"><el-input v-model="form.username" placeholder="用户名" prefix-icon="User" /></el-form-item>
        <el-form-item prop="realName"><el-input v-model="form.realName" placeholder="姓名/昵称" prefix-icon="UserFilled" /></el-form-item>
        <el-form-item prop="email"><el-input v-model="form.email" placeholder="邮箱（选填）" prefix-icon="Message" /></el-form-item>
        <el-form-item prop="password"><el-input v-model="form.password" placeholder="密码" prefix-icon="Lock" type="password" show-password /></el-form-item>
        <el-form-item prop="confirmPassword"><el-input v-model="form.confirmPassword" placeholder="确认密码" prefix-icon="Lock" type="password" show-password /></el-form-item>
        <el-form-item><el-button type="primary" style="width:100%" :loading="loading" @click="handleRegister">注 册</el-button></el-form-item>
      </el-form>
      <div style="text-align:center;margin-top:12px"><span style="color:#999;font-size:14px">已有账号？</span><el-link type="primary" @click="$router.push('/login')">返回登录</el-link></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { Connection, User, UserFilled, Message, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({ username: '', realName: '', email: '', password: '', confirmPassword: '' })
const validateConfirm = (rule, value, cb) => { if (value !== form.password) cb(new Error('两次密码不一致')); else cb() }
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '至少6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }, { validator: validateConfirm, trigger: 'blur' }]
}
const handleRegister = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await userStore.register({ username: form.username, realName: form.realName, email: form.email, password: form.password })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch {} finally { loading.value = false }
}
</script>

<style scoped>
.login-container { min-height:100vh; display:flex; align-items:center; justify-content:center; background:linear-gradient(135deg,#667eea 0%,#764ba2 100%); }
.login-card { width:420px; padding:40px; background:#fff; border-radius:12px; box-shadow:0 20px 60px rgba(0,0,0,0.2); }
.login-header { text-align:center; margin-bottom:32px; }
.login-header h1 { margin:12px 0 4px; font-size:28px; color:#333; }
.login-header p { color:#999; font-size:14px; margin:0; }
</style>
