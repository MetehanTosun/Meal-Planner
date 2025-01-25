<script setup>
import { onMounted } from 'vue'
import Sidebar from '@/components/components/Sidebar.vue'
import Workspace from '@/components/components/Workspace.vue'
import Topbar from '@/components/components/Topbar.vue'
import {authenticatedBoolean, getUserId} from '@/storage/localStorageManagement'
import { useRouter } from 'vue-router'

const router = useRouter()

const navigateTo = (page) => {
  router.push({ name: page });
};

const navigateToLogin = () => {
  router.push('/login')
}

onMounted(() => {

  authenticatedBoolean.value = !!getUserId()

  if(!authenticatedBoolean.value){
    navigateToLogin()
  }
})
</script>

<template>
  <div id="home">
    <Topbar />

    <div class="main-container">
      <Sidebar/>

      <Workspace />
    </div>

    <div class="dashboard-navigation">
      <ul class="nav">
        <li @click="navigateTo('history')">Historie</li>
        <li @click="navigateTo('statistics')">Statistik</li>
        <li @click="navigateTo('shoppingList')">Einkaufsliste</li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
*,
*::before,
*::after {
  box-sizing: border-box;
}

html,
body {
  height: 100%;
  width: 100%;
  margin: 0;
  padding: 0;
  overflow: hidden;
}

#home {
  font-family: Arial, sans-serif;
  color: #333;
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100vw;
  padding: 0;
  margin: 0;
  overflow: hidden;
}

.main-container {
  display: flex;
  width: 100%;
  height: calc(100% - (60px));
  overflow: hidden;
}

.dashboard-navigation {
  flex: 0 0 10%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #1f1f1f;
  border-top: 1px solid #333;
  z-index: 100;
}

.nav {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 0.5rem;
}

.nav li {
  list-style-type: none;
  padding: 1rem;
  background-color: #2b2b2b;
  border-radius: 8px;
  color: #fff;
  transition: background-color 0.3s ease, color 0.3s ease;
  border: 2px solid #333;
}

.nav li:hover {
  background-color: white;
  color: #1f1f1f;
  cursor: pointer;
}
</style>
