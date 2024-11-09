<template>
  <div id="app">

    <Topbar />

    <div class="main-container">

      <Sidebar :filtered-recipes="filteredRecipes" @update:filtered-recipes="updateFilteredRecipes" />


      <Workspace />

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

import { mockRecipes } from './classes/MockRecipe';
import SearchbarComponent from './components/components/SearchbarComponent.vue';
import Sidebar from './components/components/Sidebar.vue';
import Workspace from './components/components/Workspace.vue';
import Topbar from './components/components/Topbar.vue';


const filteredRecipes = ref(mockRecipes);

function updateFilteredRecipes(newFilteredRecipes) {
  console.log("Updating filtered recipes:", newFilteredRecipes);
  filteredRecipes.value = newFilteredRecipes;
}
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
