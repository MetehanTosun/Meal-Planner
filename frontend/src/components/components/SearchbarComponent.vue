<template>
  <div class="search-bar">
    <input
      type="text"
      placeholder="Suchen..."
      v-model="searchQuery"
      @input="startSearch"
    />
    <button class="icon" @click="startSearch">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 24 24"
        width="24px"
        height="24px"
        fill="currentColor"
        aria-hidden="true"
      >
        <circle
          cx="10"
          cy="10"
          r="7"
          stroke="currentColor"
          stroke-width="2"
          fill="none"
        />
        <line
          x1="16"
          y1="16"
          x2="22"
          y2="22"
          stroke="currentColor"
          stroke-width="2"
        />
      </svg>
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { mockRecipes } from '../../classes/MockRecipe.js';


const searchQuery = ref('');
const emit = defineEmits(['update:filteredRecipes']);

function startSearch(){
  if(searchQuery.value.trim()){
      const filteredRecipes = mockRecipes
          .filter(recipe => recipe.name.toLowerCase()
          .includes(searchQuery.value.trim().toLowerCase()));
      console.log(filteredRecipes);

      emit("update:filteredRecipes", filteredRecipes);
  } else {
      emit("update:filteredRecipes", mockRecipes);
  }

}

</script>

<style scoped>
  
  .search-bar {
      display: flex;
      align-items: center;
      background-color: #f1f1f1;
      border-radius: 20px;
      padding: 5px 10px;
      width: 90%;
      max-width: 200px;
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
      margin: 0 auto;
      position: relative;
  }

  .search-bar input {
      border: none;
      outline: none;
      background: none;
      font-size: 12px;
      flex: 1;
      padding: 5px;
  }

  .search-bar .icon {
      position: absolute;
      right: 10px;
      color: #888;
      font-size: 12px;
      background: none;
      border: none;
      cursor: pointer;
  }
</style>
