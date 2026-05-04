import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken, removeUser } from './auth'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      removeToken()
      removeUser()
      window.location.href = '/login'
      return Promise.reject(error)
    }
    const msg = error.response?.data?.message || '请求失败'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default request
