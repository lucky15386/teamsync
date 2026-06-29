import request from '../utils/request'

export const getUserSessions = () => request.get('/chat/sessions')

export const getSessionById = (sessionId) => request.get(`/chat/sessions/${sessionId}`)

export const getSessionMessages = (sessionId) => request.get(`/chat/sessions/${sessionId}/messages`)

export const sendMessage = (sessionId, content, type = 'TEXT') => 
  request.post(`/chat/sessions/${sessionId}/messages`, null, {
    params: { content, type }
  })

export const markAsRead = (sessionId) => request.put(`/chat/sessions/${sessionId}/read`)

export const createPrivateSession = (targetUserId) => 
  request.post(`/chat/sessions/private`, null, { params: { targetUserId } })

export const getOrCreatePrivateSession = (targetUserId) => 
  request.get(`/chat/sessions/private/${targetUserId}`)

export const getOrCreateProjectSession = (projectId) => 
  request.get(`/chat/sessions/project/${projectId}`)

// 获取可以发起聊天的用户列表和项目列表
export const getAvailableChats = () => request.get('/chat/available')
