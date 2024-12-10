<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>

</script>

<style>
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

#app {
  font-family: Arial, sans-serif;
  color: #333;
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100vw;
  padding: 0;
  margin: 0;
  overflow: hidden;
  /* Verhindert Scrollen */
}

.main-container {
  display: flex;
  width: 100%;
  height: calc(100vh - 60px);
}
</style>
