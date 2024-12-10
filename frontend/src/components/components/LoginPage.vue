Author: Ethan Banovic
Description: This component handles the entire login functionality localy, using component
api of vue 3 and axios.
<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const username = ref('')
const password = ref('')

/**
 * Attempts to log in a user by sending a POST request to the login endpoint.
 * The function sends the user's username and password in the request body using Axios.
 * If the login is successful, the server responds with user data; otherwise,
 * an error is thrown based on the response status code.
 *
 * @async
 * @function sendLogin
 * @throws {Error} Throws an error if the login attempt fails:
 *                 - "Wrong username or password!" for a 401 response.
 *                 - "Unexpected Error during login: <error details>" for other issues.
 * @returns {Promise<axios.AxiosResponse<any>>} The Axios response from the server if the request succeeds.
 */
const sendLogin = async () => {
  try {
    const response = await axios.post('http://localhost:8080/auth/login', {
      username: username.value,
      password: password.value,
    })
    return response
  } catch (error) {
    if (error.response && error.response.status === 401) {
      console.log('Wrong Login Data: ' + error)
      throw new Error('Wrong username or password!')
    } else {
      alert('An unexpected error occurred.')
      console.log('Unexpected Error during login: ' + error)
      throw new Error('Unexpected Error during login: ' + error)
    }
  }
}
/**
 * Handles the user login process by validating input fields,
 * calling the sendLogin function to authenticate the user,
 * and processing the server response.
 *
 * @async
 * @function handleLogin
 * @throws {Error} If the login attempt fails:
 *                 - Displays an alert and clears input fields for incorrect credentials.
 *                 - Displays a generic error alert for unexpected issues.
 */
const handleLogin = async () => {
  if (!username.value || !password.value) {
    alert('Please fill in both fields.')
    return
  }
  try {
    const response = await sendLogin()
    if (response.status === 200) {
      alert('Successfully logged in!')
      console.log('Logging in with:', username.value, password.value)
      router.push('/')
    }
  } catch (error) {
    if (error.message === 'Wrong username or password!') {
      alert('Wrong username or password!')
      username.value = ''
      password.value = ''
    } else {
      alert('Unexpected Error during login. Try again later.')
    }
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-container">
      <h2 class="login-title">Login</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">Username</label>
          <input type="text" id="username" v-model="username" placeholder="Enter your username" />
        </div>
        <div class="form-group">
          <label for="password">Password</label>
          <input
            type="password"
            id="password"
            v-model="password"
            placeholder="Enter your password"
          />
        </div>
        <button class="login-button" type="submit">Login</button>
      </form>
      <p class="register-link">Don't have an account? <a href="/register">Register here</a></p>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: rgba(15, 20, 30, 0.95);
}

.login-container {
  background-color: #1e293b;
  padding: 40px;
  border-radius: 15px;
  box-shadow: 0 6px 30px rgba(0, 0, 0, 0.7);
  width: 100%;
  max-width: 400px;
  text-align: center;
  transition: transform 0.2s ease-in-out;
}

.login-container:hover {
  transform: scale(1.02);
}

.login-title {
  margin-bottom: 25px;
  color: #ffffff;
  font-size: 26px;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.form-group {
  margin-bottom: 20px;
  text-align: left;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #ffffff;
  font-size: 14px;
}

.form-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #374151;
  border-radius: 8px;
  background-color: #1f2937;
  color: #ffffff;
  font-size: 14px;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
  transition:
    border-color 0.3s ease,
    box-shadow 0.3s ease;
}

.form-group input:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 6px #3b82f6;
  outline: none;
}

.form-group input::placeholder {
  color: #9ca3af;
  font-style: italic;
}

.login-button {
  background-color: #3b82f6;
  border: none;
  border-radius: 8px;
  padding: 12px 20px;
  color: white;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  width: 100%;
  margin-top: 10px;
  transition:
    background-color 0.3s ease,
    transform 0.2s ease-in-out;
}

.login-button:hover {
  background-color: #2563eb;
  transform: translateY(-2px);
}

.login-button:active {
  transform: translateY(0);
}

.register-link {
  margin-top: 20px;
  font-size: 14px;
  color: #9ca3af;
}

.register-link a {
  color: #3b82f6;
  text-decoration: none;
  font-weight: bold;
  transition: color 0.3s ease;
}

.register-link a:hover {
  color: #2563eb;
}
</style>
