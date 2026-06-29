import request from '../utils/request'

// Auth
export const login = (data) => request.post('/auth/login', data)
export const register = (data) => request.post('/auth/register', data)
