import request from '../utils/request'

export const getNotifications = () => request.get('/notifications')
export const getUnreadCount = () => request.get('/notifications/unread-count')
export const markAsRead = (id) => request.put(`/notifications/${id}/read`)
export const markAllAsRead = () => request.put('/notifications/read-all')
