import axios from 'axios'

const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080'
})

// Request Interceptor
axiosInstance.interceptors.request.use(
  config => {
    // these endpoints are public
    const publicEndpoints = ['/auth/login', '/auth/register'];

    if (!publicEndpoints.some(endpoint => config.url.includes(endpoint))) {
      const token = localStorage.getItem('token')
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// Response Interceptor
axiosInstance.interceptors.response.use(
  response => response,
  error => {
    // Nur zum Login weiterleiten, wenn es wirklich ein Auth-Problem ist
    if (error.response?.status === 401) {
      console.log('Auth error:', error.response)
      // Optional: Nur weiterleiten, wenn wir nicht schon auf der Login-Seite sind
      if (!window.location.pathname.includes('login')) {
        localStorage.removeItem('token')
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export default axiosInstance
