import request from '../utils/request'

export const getOperationLogs = () => request.get('/operation-logs')
