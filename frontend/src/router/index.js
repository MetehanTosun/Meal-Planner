import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from '@/components/components/LoginPage.vue'
import HomePage from '@/components/components/HomePage.vue'
import RegisterPage from '@/components/components/RegisterPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomePage,
    },
    {
      path: '/login',
      name: 'login',
      component: LoginPage,
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterPage,
    }
  ],
})

export default router
