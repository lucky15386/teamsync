export const formatDateTime = (value) => {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}

export const roleLabel = (role) => ({ ADMIN: '管理员', LEADER: '组长', MEMBER: '成员' }[role] || role)

export const roleTagType = (role) => ({ ADMIN: 'danger', LEADER: 'warning', MEMBER: '' }[role] || '')

export const projectStatusLabel = (status) => ({ ACTIVE: '进行中', ARCHIVED: '已归档', COMPLETED: '已完成' }[status] || status)

export const projectStatusType = (status) => ({ ACTIVE: 'success', ARCHIVED: 'info', COMPLETED: '' }[status] || 'info')

export const taskStatusLabel = (status) => ({ TODO: '待办', DOING: '进行中', DONE: '已完成' }[status] || status)

export const taskStatusType = (status) => ({ TODO: 'warning', DOING: '', DONE: 'success' }[status] || 'info')

export const priorityLabel = (priority) => ({ HIGH: '高', MED: '中', LOW: '低' }[priority] || priority)

export const priorityType = (priority) => ({ HIGH: 'danger', MED: 'warning', LOW: 'info' }[priority] || '')

export const formatFileSize = (bytes) => {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / 1048576).toFixed(1) + ' MB'
}

export const displayName = (user) => user?.realName || user?.username || '未知'
