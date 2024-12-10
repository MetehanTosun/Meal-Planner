<template>
    <div class="topbar">
      <div class="spacer"></div>
      <button class="login-button" @click="navigateToLogin">Login</button>
    </div>
</template>

<script>
import {useRouter} from 'vue-router'

export default {
    setup() {
      const router = useRouter();

      const navigateToLogin = () => {
        router.push('/login'); // Navigate to the login page
      };

      return {
        navigateToLogin,
      };
    },

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
.spacer{
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
</style>
