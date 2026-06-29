<template>
  <div :class="{ 'chat-container-dark': userStore.isDark }" class="chat-container">
    <div class="chat-sidebar">
      <div class="sidebar-header">
        <h3 :class="{ 'text-dark': userStore.isDark }">聊天</h3>
        <el-button type="primary" size="small" circle @click="newChatDialogVisible = true; loadAvailableChats()">
          <el-icon><Plus /></el-icon>
        </el-button>
      </div>
      <div class="chat-list">
        <div 
          v-for="chat in chatList" 
          :key="chat.id" 
          :class="['chat-item', { 'active': activeChatId === chat.id, 'chat-item-dark': userStore.isDark }]"
          @click="selectChat(chat.id)"
        >
          <el-avatar :size="40" :src="chat.avatar">{{ (chat.name || 'U').charAt(0) }}</el-avatar>
          <div class="chat-info">
            <div class="chat-name" :class="{ 'text-dark': userStore.isDark }">{{ chat.name }}</div>
            <div class="chat-last-message" :class="{ 'text-muted-dark': userStore.isDark }">{{ chat.lastMessage || '' }}</div>
          </div>
          <div v-if="chat.unreadCount > 0" class="unread-badge">{{ chat.unreadCount }}</div>
        </div>
      </div>
    </div>
    <div class="chat-main">
      <div v-if="!activeChatId" class="chat-empty" :class="{ 'chat-empty-dark': userStore.isDark }">
        <el-icon :size="80" color="#909399"><ChatDotRound /></el-icon>
        <p :style="{ color: userStore.isDark ? '#9ca3af' : '#909399', marginTop: '20px' }">选择一个聊天开始对话</p>
      </div>
      <div v-else class="chat-content">
        <div class="chat-header" :class="{ 'chat-header-dark': userStore.isDark }">
          <div class="chat-user-info">
            <el-avatar :size="40" :src="activeChat?.avatar">{{ (activeChat?.name || 'U').charAt(0) }}</el-avatar>
            <span :class="{ 'text-dark': userStore.isDark }">{{ activeChat?.name }}</span>
          </div>
        </div>
        <div class="messages-container" ref="messagesContainer" :class="{ 'messages-container-dark': userStore.isDark }">
          <div v-for="(msg, index) in currentMessages" :key="msg.id || index" :class="['message-item', msg.isSelf ? 'message-self' : 'message-other']">
            <div v-if="!msg.isSelf" class="message-avatar">
              <el-avatar :size="36" :src="msg.senderAvatar || activeChat?.avatar">
                {{ (msg.senderName || activeChat?.name || 'U').charAt(0) }}
              </el-avatar>
            </div>
            <div class="message-wrapper">
              <div v-if="!msg.isSelf" class="message-sender" :class="{ 'text-dark': userStore.isDark }">
                {{ msg.senderName }}
              </div>
              <div :class="['message-bubble', userStore.isDark ? (msg.isSelf ? 'bubble-self-dark' : 'bubble-other-dark') : '']">
                {{ msg.content }}
              </div>
              <div class="message-time" :class="{ 'text-muted-dark': userStore.isDark }">{{ msg.time }}</div>
            </div>
            <div v-if="msg.isSelf" class="message-avatar">
              <el-avatar :size="36">{{ (userStore.user?.realName || userStore.user?.username || 'U').charAt(0) }}</el-avatar>
            </div>
          </div>
        </div>
        <div class="chat-input-area" :class="{ 'chat-input-area-dark': userStore.isDark }">
          <div class="input-wrapper">
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="3"
              placeholder="输入消息..."
              @keydown.enter.prevent="sendMessage"
              :class="{ 'input-dark': userStore.isDark }"
            />
          </div>
          <div class="input-actions">
            <el-button type="primary" :disabled="!inputMessage.trim()" @click="sendMessage">
              <el-icon><Promotion /></el-icon> 发送
            </el-button>
          </div>
        </div>
      </div>
    </div>
    <!-- 新建聊天对话框 -->
    <el-dialog v-model="newChatDialogVisible" title="开始新聊天" width="500px">
      <el-tabs v-model="newChatTab">
        <el-tab-pane label="用户" name="user">
          <div class="user-list">
            <div 
              v-for="user in availableUsers" 
              :key="user.id"
              class="user-item"
              @click="startPrivateChat(user)"
            >
              <el-avatar :size="40">{{ (user.realName || user.username || 'U').charAt(0) }}</el-avatar>
              <div class="user-info">
                <div class="user-name">{{ user.realName || user.username }}</div>
                <div class="user-username">{{ user.username }}</div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="项目群聊" name="project">
          <div class="project-list">
            <div 
              v-for="project in availableProjects" 
              :key="project.id"
              class="project-item"
              @click="startProjectChat(project)"
            >
              <el-avatar :size="40" :icon="Folder" />
              <div class="project-info">
                <div class="project-name">{{ project.name }}</div>
                <div class="project-desc">{{ project.description || '项目群聊' }}</div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { ChatDotRound, Promotion, Plus, User, Folder } from '@element-plus/icons-vue'
import { getUserSessions, getSessionMessages, sendMessage as sendApiMessage, markAsRead, getOrCreatePrivateSession, getOrCreateProjectSession } from '../api/chat'
import { getUserOptions } from '../api/user'
import { getProjects } from '../api/project'
import { formatDateTime } from '../utils/format'

const userStore = useUserStore()
const activeChatId = ref(null)
const inputMessage = ref('')
const messagesContainer = ref(null)
const loading = ref(false)
const chatList = ref([])
const messagesMap = ref({})
const newChatDialogVisible = ref(false)
const newChatTab = ref('user')
const availableUsers = ref([])
const availableProjects = ref([])

const activeChat = computed(() => chatList.value.find(c => c.id === activeChatId.value))
const currentMessages = computed(() => messagesMap.value[activeChatId.value] || [])

const loadSessions = async () => {
  try {
    const res = await getUserSessions()
    chatList.value = res.data || []
  } catch (error) {
    console.error('加载聊天列表失败', error)
  }
}

const loadAvailableChats = async () => {
  try {
    const [usersRes, projectsRes] = await Promise.all([
      getUserOptions(),
      getProjects()
    ])
    availableUsers.value = (usersRes.data || []).filter(u => u.id !== userStore.user?.id)
    availableProjects.value = projectsRes.data || []
  } catch (error) {
    console.error('加载可用聊天对象失败', error)
  }
}

const selectChat = async (id) => {
  activeChatId.value = id
  const chat = chatList.value.find(c => c.id === id)
  if (chat && chat.unreadCount > 0) {
    try {
      await markAsRead(id)
      chat.unreadCount = 0
    } catch (e) {
      console.error('标记已读失败', e)
    }
  }
  if (!messagesMap.value[id]) {
    await loadMessages(id)
  }
  nextTick(() => scrollToBottom())
}

const loadMessages = async (sessionId) => {
  try {
    const res = await getSessionMessages(sessionId)
    messagesMap.value[sessionId] = (res.data || []).map(msg => ({
      id: msg.id,
      isSelf: msg.senderId === userStore.user?.id,
      content: msg.content,
      time: formatDateTime(msg.createTime, 'HH:mm'),
      fullTime: formatDateTime(msg.createTime),
      senderName: msg.senderName,
      senderAvatar: msg.senderAvatar
    }))
  } catch (error) {
    console.error('加载消息失败', error)
  }
}

const startPrivateChat = async (targetUser) => {
  try {
    const res = await getOrCreatePrivateSession(targetUser.id)
    const session = res.data
    // 检查是否已存在
    if (!chatList.value.find(c => c.id === session.id)) {
      chatList.value.unshift(session)
    }
    newChatDialogVisible.value = false
    selectChat(session.id)
    ElMessage.success('已开始聊天')
  } catch (error) {
    console.error('创建私人聊天失败', error)
    ElMessage.error('创建聊天失败')
  }
}

const startProjectChat = async (project) => {
  try {
    const res = await getOrCreateProjectSession(project.id)
    const session = res.data
    if (!chatList.value.find(c => c.id === session.id)) {
      chatList.value.unshift(session)
    }
    newChatDialogVisible.value = false
    selectChat(session.id)
    ElMessage.success('已进入项目群聊')
  } catch (error) {
    console.error('进入项目群聊失败', error)
    ElMessage.error('进入群聊失败')
  }
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || !activeChatId.value || loading.value) return
  loading.value = true
  try {
    const res = await sendApiMessage(activeChatId.value, inputMessage.value)
    const newMsg = res.data
    const time = formatDateTime(newMsg.createTime, 'HH:mm')
    if (!messagesMap.value[activeChatId.value]) {
      messagesMap.value[activeChatId.value] = []
    }
    messagesMap.value[activeChatId.value].push({
      id: newMsg.id,
      isSelf: true,
      content: newMsg.content,
      time,
      fullTime: formatDateTime(newMsg.createTime),
      senderName: userStore.user?.realName || userStore.user?.username,
      senderAvatar: ''
    })
    const chat = chatList.value.find(c => c.id === activeChatId.value)
    if (chat) {
      chat.lastMessage = newMsg.content
    }
    inputMessage.value = ''
    nextTick(() => scrollToBottom())
  } catch (error) {
    console.error('发送失败', error)
    ElMessage.error('发送失败')
  } finally {
    loading.value = false
  }
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    nextTick(() => {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    })
  }
}

onMounted(() => {
  loadSessions()
})
</script>

<style scoped>
.chat-container {
  display: flex;
  height: 100%;
  background-color: #f5f7fa;
}
.chat-container-dark {
  background-color: #111827;
}
.chat-sidebar {
  width: 280px;
  border-right: 1px solid #e5e7eb;
  background-color: #fff;
  display: flex;
  flex-direction: column;
}
.chat-container-dark .chat-sidebar {
  background-color: #1f2937;
  border-right-color: #374151;
}
.sidebar-header {
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.chat-container-dark .sidebar-header {
  border-bottom-color: #374151;
}
.sidebar-header h3 {
  margin: 0;
  color: #333;
}
.text-dark {
  color: #e5e7eb !important;
}
.chat-list {
  flex: 1;
  overflow-y: auto;
}
.chat-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  border-bottom: 1px solid #f3f4f6;
  transition: background-color 0.2s;
}
.chat-item:hover {
  background-color: #f3f4f6;
}
.chat-item.active {
  background-color: #eff6ff;
}
.chat-item-dark {
  border-bottom-color: #374151;
}
.chat-item-dark:hover {
  background-color: #374151;
}
.chat-item-dark.active {
  background-color: #1e3a5f;
}
.chat-info {
  flex: 1;
  margin-left: 12px;
  overflow: hidden;
}
.chat-name {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}
.chat-last-message {
  font-size: 12px;
  color: #9ca3af;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.text-muted-dark {
  color: #6b7280 !important;
}
.unread-badge {
  background-color: #f56565;
  color: #fff;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  min-width: 20px;
  text-align: center;
}
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
}
.chat-container-dark .chat-main {
  background-color: #111827;
}
.chat-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #fff;
}
.chat-empty-dark {
  background-color: #111827;
}
.chat-header {
  padding: 12px 20px;
  border-bottom: 1px solid #e5e7eb;
  background-color: #fff;
}
.chat-header-dark {
  background-color: #1f2937;
  border-bottom-color: #374151;
}
.chat-user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}
.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f9fafb;
}
.messages-container-dark {
  background-color: #111827;
}
.message-item {
  display: flex;
  margin-bottom: 16px;
}
.message-self {
  flex-direction: row-reverse;
}
.message-avatar {
  flex-shrink: 0;
}
.message-wrapper {
  max-width: 60%;
  margin: 0 12px;
}
.message-self .message-wrapper {
  text-align: right;
}
.message-bubble {
  padding: 10px 14px;
  border-radius: 12px;
  word-wrap: break-word;
  display: inline-block;
}
.message-other .message-bubble {
  background-color: #fff;
  color: #333;
  border-bottom-left-radius: 4px;
}
.message-self .message-bubble {
  background-color: #409eff;
  color: #fff;
  border-bottom-right-radius: 4px;
}
.bubble-other-dark {
  background-color: #374151 !important;
  color: #e5e7eb !important;
}
.bubble-self-dark {
  background-color: #1e40af !important;
  color: #fff !important;
}
.message-sender {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 4px;
}
.message-time {
  font-size: 12px;
  color: #9ca3af;
  margin-top: 4px;
}
.chat-input-area {
  padding: 16px 20px;
  border-top: 1px solid #e5e7eb;
  background-color: #fff;
}
.chat-input-area-dark {
  background-color: #1f2937;
  border-top-color: #374151;
}
.input-wrapper {
  margin-bottom: 12px;
}
.input-dark :deep(.el-textarea__inner) {
  background-color: #374151 !important;
  border-color: #4b5563 !important;
  color: #e5e7eb !important;
}
.input-dark :deep(.el-textarea__inner::placeholder) {
  color: #6b7280 !important;
}
.input-actions {
  display: flex;
  justify-content: flex-end;
}
/* 新建聊天对话框样式 */
.user-list,
.project-list {
  max-height: 400px;
  overflow-y: auto;
}
.user-item,
.project-item {
  display: flex;
  align-items: center;
  padding: 12px;
  cursor: pointer;
  border-radius: 8px;
  transition: background-color 0.2s;
  margin-bottom: 8px;
}
.user-item:hover,
.project-item:hover {
  background-color: #f3f4f6;
}
.user-info,
.project-info {
  margin-left: 12px;
}
.user-name,
.project-name {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}
.user-username,
.project-desc {
  font-size: 12px;
  color: #9ca3af;
}
</style>
