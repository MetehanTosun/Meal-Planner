<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/axios'

const router = useRouter()
const username = ref('')
const password = ref('')

const sendNewUser = async () => {
  console.log('Attempt Registration: ', {
    username: username.value,
    password: password.value
  })

  return await axios.post('/auth/register', {
    username: username.value,
    password: password.value,
  })
}

const handleRegister = async () => {
  if (!username.value || !password.value) {
    alert('Bitte fülle alle Felder aus!')
    return
  }

  try {
    const response = await sendNewUser()
    console.log('Registration response:', response)

    if (response.status === 200) {
      alert('Dein Account wurde erstellt!')
      router.push('/login')
    }
  } catch (err) {
    console.error('Failed to Register:', err.response?.data || err)
    alert(err.response?.data?.message || 'Registrierung fehlgeschlagen!')
  }
}
</script>

<template>
  <div class="register-page">
    <div class="register-container">
      <h2 class="register-title">Register</h2>
      <form @submit.prevent="handleRegister">
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
        <button class="register-button" type="submit">Register</button>
      </form>
      <p class="login-link">Already have an account? <a href="/login">Login here</a></p>
    </div>
  </div>
</template>

<style scoped>
.register-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: rgba(15, 20, 30, 0.95);
}

.register-container {
  background-color: #1e293b;
  padding: 40px;
  border-radius: 15px;
  box-shadow: 0 6px 30px rgba(0, 0, 0, 0.7);
  width: 100%;
  max-width: 400px;
  text-align: center;
  transition: transform 0.2s ease-in-out;
}

.register-container:hover {
  transform: scale(1.02);
}

.register-title {
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

.register-button {
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

.register-button:hover {
  background-color: #2563eb;
  transform: translateY(-2px);
}

.register-button:active {
  transform: translateY(0);
}

.login-link {
  margin-top: 20px;
  font-size: 14px;
  color: #9ca3af;
}

.login-link a {
  color: #3b82f6;
  text-decoration: none;
  font-weight: bold;
  transition: color 0.3s ease;
}

.login-link a:hover {
  color: #2563eb;
}
</style>
