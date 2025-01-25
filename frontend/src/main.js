import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia';
import Toast from "vue-toastification";
import "vue-toastification/dist/index.css";


const app = createApp(App)
const pinia = createPinia();
const toastOptions = {
  transition: "Vue-Toastification__bounce",
  maxToasts: 1,
  newestOnTop: true,
};

app.use(pinia);
app.use(router)
app.use(Toast, toastOptions)

app.mount('#app')
