<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <div class="logo-wrapper">
          <el-icon size="48" color="#409eff"><Connection /></el-icon>
        </div>
        <h1>TeamSync</h1>
        <p>高效团队协作，从此刻开始</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" size="large">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" class="login-input" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" placeholder="请输入密码" prefix-icon="Lock" type="password" show-password @keyup.enter="handleLogin" class="login-input" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-button" :loading="loading" @click="handleLogin">
            <span v-if="!loading">登 录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form-item>
      </el-form>
      <div class="register-link">
        <span>还没有账号？</span>
        <el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
      </div>
      <div class="demo-hint">
        <el-icon><InfoFilled /></el-icon>
        <span>演示账号：admin / zhangsan / lisi，密码均为 123456</span>
      </div>
    </div>
    <div class="decoration-circle decoration-1"></div>
    <div class="decoration-circle decoration-2"></div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { Connection, User, Lock, InfoFilled } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = { 
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }], 
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }] 
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try { 
    await userStore.login(form)
    ElMessage.success('登录成功')
    router.push('/')
  } catch {} 
  finally { 
    loading.value = false 
  }
}
</script>

<style scoped>
.login-container { 
  min-height: 100vh; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); 
  position: relative;
  overflow: hidden;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.decoration-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  right: -100px;
}

.decoration-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  left: -50px;
}

.login-card { 
  width: 420px; 
  padding: 50px 40px; 
  background: #fff; 
  border-radius: 20px; 
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.3);
  position: relative;
  z-index: 1;
}

.login-header { 
  text-align: center; 
  margin-bottom: 40px; 
}

.logo-wrapper {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.login-header h1 { 
  margin: 0 0 8px; 
  font-size: 32px; 
  color: #333; 
  font-weight: 700;
  letter-spacing: 1px;
}

.login-header p { 
  color: #999; 
  font-size: 15px; 
  margin: 0;
}

.login-input :deep(.el-input__wrapper) {
  padding: 12px 16px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.login-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.login-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #667eea, 0 4px 12px rgba(102, 126, 234, 0.2);
}

.login-button {
  width: 100%;
  padding: 14px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.register-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.demo-hint {
  margin-top: 24px;
  padding: 16px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  border-radius: 10px;
  font-size: 13px;
  color: #666;
  text-align: center;
  line-height: 1.8;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.demo-hint .el-icon {
  color: #667eea;
}
</style>
