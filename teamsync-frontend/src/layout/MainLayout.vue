<template>
  <el-container style="height: 100vh">
    <el-aside :width="isCollapse ? '64px' : '220px'" :style="{ transition: 'width 0.3s', background: userStore.isDark ? '#1f2937' : '#304156' }">
      <div class="logo" :class="{ collapsed: isCollapse }">
        <el-icon size="24" color="#409eff"><Connection /></el-icon>
        <span v-show="!isCollapse" class="logo-text">TeamSync</span>
      </div>
      <el-menu 
        :default-active="$route.path" 
        :background-color="userStore.isDark ? '#1f2937' : '#304156'" 
        text-color="#bfcbd9" 
        active-text-color="#409eff" 
        :collapse="isCollapse" 
        router
      >
        <el-menu-item index="/dashboard"><el-icon><Odometer /></el-icon><template #title>工作台</template></el-menu-item>
        <el-menu-item index="/projects"><el-icon><Folder /></el-icon><template #title>项目管理</template></el-menu-item>
        <el-menu-item index="/tasks"><el-icon><Document /></el-icon><template #title>我的任务</template></el-menu-item>
        <el-menu-item index="/documents"><el-icon><Files /></el-icon><template #title>文档管理</template></el-menu-item>
        <el-menu-item index="/chat"><el-icon><ChatDotRound /></el-icon><template #title>聊天</template></el-menu-item>
        <el-menu-item index="/notifications"><el-icon><Bell /></el-icon><template #title>消息通知</template></el-menu-item>
        <el-menu-item v-if="userStore.isAdmin" index="/users"><el-icon><User /></el-icon><template #title>用户管理</template></el-menu-item>
        <el-menu-item v-if="userStore.isAdmin" index="/operation-logs"><el-icon><List /></el-icon><template #title>操作日志</template></el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header :style="{ display:'flex',alignItems:'center',justifyContent:'space-between',borderBottom: userStore.isDark ? '1px solid #374151' : '1px solid #eee', background: userStore.isDark ? '#1f2937' : '#fff' }">
        <div style="display:flex;align-items:center">
          <el-icon size="20" style="cursor:pointer;margin-right:16px" @click="isCollapse=!isCollapse"><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
          <span :style="{ fontSize:'16px', fontWeight:'600', color: userStore.isDark ? '#e5e7eb' : '#333' }">{{ $route.meta.title }}</span>
        </div>
        <div style="display:flex;align-items:center;gap:16px">
          <el-tooltip :content="userStore.isDark ? '切换到浅色模式' : '切换到深色模式'">
            <el-icon :size="20" style="cursor:pointer" @click="userStore.toggleTheme">
              <Sunny v-if="userStore.isDark" /><Moon v-else />
            </el-icon>
          </el-tooltip>
          <el-badge :value="unreadCount" :hidden="unreadCount===0" :max="99">
            <el-icon :size="20" style="cursor:pointer" @click="$router.push('/notifications')"><Bell /></el-icon>
          </el-badge>
          <el-dropdown @command="handleCommand">
            <span style="display:flex;align-items:center;gap:8px;cursor:pointer">
              <el-avatar :size="32">{{ userStore.user?.realName?.charAt(0) || 'U' }}</el-avatar>
              <span :style="{ color: userStore.isDark ? '#e5e7eb' : '#333' }">{{ userStore.user?.realName || userStore.user?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main :style="{ background: userStore.isDark ? '#111827' : '#f5f7fa', padding: $route.path === '/chat' ? '0' : '20px' }"><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getUnreadCount } from '../api/notification'
import { Connection, Odometer, Folder, Document, Files, ChatDotRound, Bell, User, List, Fold, Expand, Sunny, Moon } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)
const unreadCount = ref(0)

const fetchUnread = async () => {
  try { const res = await getUnreadCount(); unreadCount.value = res.data?.count ?? res.data ?? 0 } catch {}
}
onMounted(() => { fetchUnread(); setInterval(fetchUnread, 60000) })

const handleCommand = (cmd) => {
  if (cmd === 'profile') router.push('/profile')
  else if (cmd === 'logout') { userStore.logout(); router.push('/login') }
}
</script>

<style scoped>
.logo { height:60px; display:flex; align-items:center; justify-content:center; gap:8px; color:#fff; font-size:20px; font-weight:700; border-bottom:1px solid rgba(255,255,255,0.1); }
.logo.collapsed .logo-text { display:none; }
.el-menu { border-right:none; }
</style>
