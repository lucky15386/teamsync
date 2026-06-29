import request from '../utils/request'

export const getCurrentUser = () => request.get('/users/me')
export const getUserList = () => request.get('/users')
export const getUserOptions = () => request.get('/users/options')
export const getUserById = (id) => request.get(`/users/${id}`)
export const updateUser = (id, data) => request.put(`/users/${id}`, data)
export const changePassword = (id, data) => request.put(`/users/${id}/password`, data)
export const updateUserStatus = (id, status) => request.put(`/users/${id}/status?status=${status}`)
export const deleteUser = (id) => request.delete(`/users/${id}`)
