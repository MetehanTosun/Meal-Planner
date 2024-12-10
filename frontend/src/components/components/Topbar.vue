<template>
  <div class="topbar">
    <div class="spacer"></div>
    <button class="login-button" v-if="!isAuthenticated" @click="navigateToLogin">Login</button>
    <button class="logout-button" v-if="isAuthenticated" @click="handleLogout">Logout</button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import {isAuthenticated, clearUserId} from '@/storage/userStorage.js'
const router = useRouter()

const navigateToLogin = () => {
  router.push('/login')
}

const handleLogout = () => {
  alert('Successfully logged out!')
  clearUserId()
  router.push('login')
}
</script>

<style>
.topbar {
  background-color: rgba(15, 20, 30, 0.95);
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  color: white;
  width: 100%;
  justify-content: space-between;
}

.spacer {
  flex: 1;
}

.login-button {
  background-color: #1e90ff;
  border: none;
  border-radius: 5px;
  padding: 10px 20px;
  color: white;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

.login-button:hover {
  background-color: #0073e6;
}

.logout-button {
  background-color: #ff970f;
  border: none;
  border-radius: 5px;
  padding: 10px 20px;
  color: white;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

.logout-button:hover {
  background-color: #ff970f;
}
</style>
