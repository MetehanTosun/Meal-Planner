<template>
  <div id="app">

    <div class="topbar">

    </div>


    <div class="main-container">

      <Sidebar
        :filtered-recipes="filteredRecipes"
        @update:filtered-recipes="updateFilteredRecipes"
      />


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


const filteredRecipes = ref(mockRecipes);

function updateFilteredRecipes(newFilteredRecipes){
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
    overflow: hidden; /* Verhindert Scrollen */
  }

  .topbar {
    background-color: #444;
    height: 60px;
    display: flex;
    align-items: center;
    padding: 0 20px;
    color: white;
    width: 100%;
  }

  .main-container {
    display: flex;
    width: 100%; 
    height: calc(100vh - 60px); 
  }

  .sidebar {
    width: 250px; 
    background-color: #ddd;
    padding: 10px;
    box-sizing: border-box;
  }

  .sidebar-header {
    font-weight: bold;
    margin-bottom: 10px;
    background-color: #888;
    padding: 5px;
    color: white;
  }

  .recipe-list {
    list-style-type: none;
    padding: 0;
    margin: 0; 
  }

  .recipe-list li {
    margin: 10px 0;
    cursor: pointer;
  }

  .recipe-element {
    background-color: rgba(135, 163, 223, 0.64);
    color: #333;
    border-radius: 10px;
    display: flex;
    justify-content: center;
    align-content: end;
  }

  .content {
    flex: 1; 
    padding: 20px;
    background-color: #f9f9f9;
    display: flex;
    flex-direction: column;
  }

  .menu-bar {
    display: flex;
    justify-content: space-around;
    margin-bottom: 20px;
    flex-wrap: wrap;
  }

  .menu-item {
    width: 60px;
    height: 60px;
    background-color: #ccc;
    border-radius: 5px;
    margin: 5px;
  }
</style>
