<script setup>
import { ref } from 'vue'

import { mockRecipes } from '@/classes/MockRecipe'
import Sidebar from '@/components/components/Sidebar.vue'
import Workspace from '@/components/components/Workspace.vue'
import Topbar from '@/components/components/Topbar.vue'

const filteredRecipes = ref(mockRecipes)

function updateFilteredRecipes(newFilteredRecipes) {
  console.log('Updating filtered recipes:', newFilteredRecipes)
  filteredRecipes.value = newFilteredRecipes
}
</script>

<template>
  <div id="home">
    <Topbar />

    <div class="main-container">
      <Sidebar
        :filtered-recipes="filteredRecipes"
        @update:filtered-recipes="updateFilteredRecipes"
      />

      <Workspace />
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
  height: calc(100vh - 60px);
}
</style>
