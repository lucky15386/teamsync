import request from '../utils/request'

export const createTask = (data) => request.post('/tasks', data)
export const getTaskById = (id) => request.get(`/tasks/${id}`)
export const getTasksByProject = (projectId) => request.get(`/tasks/project/${projectId}`)
export const getTasksByProjectAndStatus = (projectId, status) => request.get(`/tasks/project/${projectId}/status/${status}`)
export const getMyTasks = () => request.get('/tasks/my')
export const updateTask = (id, data) => request.put(`/tasks/${id}`, data)
export const updateTaskStatus = (id, status) => request.put(`/tasks/${id}/status`, { status })
export const deleteTask = (id) => request.delete(`/tasks/${id}`)
export const getTaskComments = (id) => request.get(`/tasks/${id}/comments`)
export const addTaskComment = (id, data) => request.post(`/tasks/${id}/comments`, data)
export const deleteTaskComment = (id) => request.delete(`/tasks/comments/${id}`)
export const getProjectTaskStats = (projectId) => request.get(`/tasks/project/${projectId}/stats`)
