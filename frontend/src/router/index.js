import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from '@/components/components/LoginPage.vue'
import HomePage from '@/components/components/HomePage.vue'
import RegisterPage from '@/components/components/RegisterPage.vue'
import UserStatistics from '@/components/components/UserStatistics.vue'
import ShoppingList from '@/components/components/ShoppingList.vue'
import History from "@/components/components/History.vue";

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
    },
    {
      path: '/statistics',
      name: 'statistics',
      component: UserStatistics,
    },
    {
      path: '/shopping-list',
      name: 'shoppingList',
      component: ShoppingList,
    },
    {
      path: '/history',
      name: 'history',
      component: History,
    },
  ],
})

export default router
