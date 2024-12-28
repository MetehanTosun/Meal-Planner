<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/axios'
import { getUserId, setUserId } from '@/storage/localStorageManagement.js'
import { useWeekStore } from '@/state-management/index.js'

const router = useRouter()
const username = ref('')
const password = ref('')
const errorMessage = ref('')
useWeekStore()
const sendLogin = async () => {
  try {
    return await axios.post('/auth/login', {
      username: username.value,
      password: password.value,
    })
  } catch (error) {
    console.error('Login error:', error)
    throw error
  }
}

const handleLogin = async () => {
  if (!username.value || !password.value) {
    errorMessage.value = 'Please fill in both fields.';
    return;
  }

  try {
    const response = await sendLogin();
    if (response.data.userId) {
      console.log('Login successful. Received userId:', response.data.userId);
      setUserId(response.data.userId); // Save to localStorage
      await fetchCurrentWeek(getUserId()); // Fetch the current week immediately after login
      await router.push('/'); // Redirect to home page
    } else {
      errorMessage.value = 'Login failed. Please check your credentials.';
    }
  } catch (error) {
    console.error('Login error:', error);
    errorMessage.value = error.response?.data || 'Login failed. Please try again.';
    username.value = '';
    password.value = '';
  }
};
async function fetchCurrentWeek() {
  try {
    console.log('Fetching current week for userId:', getUserId());
    const response = await axios.get(`/weeks/current/${getUserId()}`);
    const weekStore = useWeekStore();
    weekStore.setWeekData(response.data); // Store week data in Pinia
    console.log('Current week fetched successfully:', response.data);
  } catch (error) {
    console.error('Error fetching current week:', error);
    throw error;
  }
};
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
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
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

.error-message {
  color: #ef4444;
  margin-bottom: 15px;
  font-size: 14px;
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
