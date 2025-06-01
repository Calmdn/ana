import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_APP_API_BASE_URL || 'http://localhost:8080'

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    const { data } = response
    if (data.success) {
      return data.data
    } else {
      throw new Error(data.message || '请求失败')
    }
  },
  error => {
    console.error('API Error:', error)
    throw error
  }
)

export default api