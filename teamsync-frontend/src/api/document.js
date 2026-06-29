import request from '../utils/request'

export const uploadDocument = (projectId, file, category) => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('projectId', projectId)
  if (category) formData.append('category', category)
  return request.post('/documents/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
export const getDocuments = () => request.get('/documents')
export const getDocumentsByProject = (projectId) => request.get(`/documents/project/${projectId}`)
export const getDocumentById = (id) => request.get(`/documents/${id}`)
export const deleteDocument = (id) => request.delete(`/documents/${id}`)
export const downloadDocument = (id) => { window.open(`/api/documents/download/${id}`, '_blank') }
