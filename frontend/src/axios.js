import axios from 'axios'
import { getUserId } from '@/storage/localStorageManagement.js'

const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request Interceptor which adds the User Id
axiosInstance.interceptors.request.use(
  config => {

    if (!config.url?.includes('/auth/')) {
      const userId = getUserId()
      if (userId) {
        config.headers['User-Id'] = userId
      }
    }
    console.log('Making request with config:', config)
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

export default axiosInstance
