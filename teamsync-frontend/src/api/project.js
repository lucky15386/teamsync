import request from '../utils/request'

export const createProject = (data) => request.post('/projects', data)
export const getMyProjects = () => request.get('/projects')
export const getAllProjects = () => request.get('/projects/all')
export const getProjectById = (id) => request.get(`/projects/${id}`)
export const updateProject = (id, data) => request.put(`/projects/${id}`, data)
export const deleteProject = (id) => request.delete(`/projects/${id}`)
export const getProjectMembers = (id) => request.get(`/projects/${id}/members`)
export const addProjectMember = (id, data) => request.post(`/projects/${id}/members`, data)
export const removeProjectMember = (id, userId) => request.delete(`/projects/${id}/members/${userId}`)
export const getProjectStats = (id) => request.get(`/projects/${id}/stats`)
