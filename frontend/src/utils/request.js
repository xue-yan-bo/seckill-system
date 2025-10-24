import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    console.error('请求错误', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果code不是200，说明请求失败
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      
      // 401: Token过期或未登录
      if (res.code === 401) {
        localStorage.removeItem('token')
        router.push('/login')
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return res
  },
  error => {
    console.error('响应错误', error)
    
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('未登录或Token已过期')
          localStorage.removeItem('token')
          router.push('/login')
          break
        case 403:
          ElMessage.error('没有权限')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
        default:
          ElMessage.error('请求失败')
      }
    } else {
      ElMessage.error('网络错误')
    }
    
    return Promise.reject(error)
  }
)

export default service

