import axios from 'axios'
import { getUserId } from '@/storage/localStorageManagement.js'

const axiosInstance = axios.create({
  baseURL: 'http://[2001:7c0:2320:1:f816:3eff:fe54:3865]:8080',
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
