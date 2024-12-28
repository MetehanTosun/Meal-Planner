<script setup>

</script>

<template>
  <div class="container">
    <div class="module">
      <ul class="nav">
        <li class="nav-item">
          Planner
        </li>
        <li class="nav-item">
          Statistics
        </li>
        <li class="nav-item">
          Receipt
        </li>
      </ul>
    </div>
  </div>
</template>
<style scoped>
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 2rem;
  height: 15%;
  overflow: hidden;
  padding-left: 2rem;
  padding-right: 2rem;
  padding-bottom: 4rem;
}
.module {
  width: 20%;
  align-items: center;
  gap: 2rem;
  background-color: #1f1f1f;
  border: white solid 1px;
  border-radius: 10px;
}
.nav {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  padding: 0.5rem;
}
.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #1f1f1f;
  padding: 1rem;
  color: #fff;
  border-radius: 10px;
  transition: background-color 0.3s ease, color 0.3s ease;
}
.nav-item:hover {
  background-color: white;
  color: #1f1f1f;
  cursor: pointer;
}
</style>
