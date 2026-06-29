import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = 'Bearer ' + token
  return config
})

request.interceptors.response.use(
  res => {
    // 如果是 blob 类型响应，直接返回原始 response
    if (res.config.responseType === 'blob') {
      // 检查是否是错误响应（content-type 是 json）
      const contentType = res.headers['content-type']
      if (contentType && contentType.includes('application/json')) {
        return new Promise((resolve, reject) => {
          const reader = new FileReader()
          reader.onload = () => {
            try {
              const result = JSON.parse(reader.result)
              ElMessage.error(result.message || '请求失败')
              if (result.code === 401) {
                localStorage.removeItem('token')
                router.push('/login')
              }
              reject(result)
            } catch (e) {
              reject(e)
            }
          }
          reader.readAsText(res.data)
        })
      }
      return res.data // 返回 blob
    }
    // 正常 JSON 响应处理
    if (res.data.code && res.data.code !== 200) {
      ElMessage.error(res.data.message || '请求失败')
      if (res.data.code === 401) {
        localStorage.removeItem('token')
        router.push('/login')
      }
      return Promise.reject(res.data)
    }
    return res.data
  },
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      router.push('/login')
    }
    ElMessage.error(err.response?.data?.message || '网络错误')
    return Promise.reject(err)
  }
)

export default request
