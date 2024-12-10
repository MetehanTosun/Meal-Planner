<template>
  <div class="search-bar">
    <input type="text" placeholder="Suchen..." v-model="searchQuery" @input="startSearch" />
    <button class="icon" @click="startSearch">
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24px" height="24px" fill="currentColor"
        aria-hidden="true">
        <circle cx="10" cy="10" r="7" stroke="currentColor" stroke-width="2" fill="none" />
        <line x1="16" y1="16" x2="22" y2="22" stroke="currentColor" stroke-width="2" />
      </svg>
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { mockRecipes } from '../../classes/MockRecipe.js';
import { fetchRecipes } from '../../classes/ApiController.js';

const searchQuery = ref('');
const emit = defineEmits(['update:filtered-recipes']);

/*
const props = defineProps({
  dietFilter: {
    type: String,
    required: false,
    default: null,
  },
});
*/
async function startSearch() {
  if (searchQuery.value.trim()) {
    try {
      // API query for real recipes
      const filteredRecipes = await fetchRecipes(searchQuery.value);
      console.log("Rezepte von API:", filteredRecipes);

      // Uses the Mockrecipes if the Api does not have a response
      if (filteredRecipes.length === 0) {
        console.warn("Keine API-Ergebnisse gefunden. Verwende Mockrezepte.");
        emit("update:filteredRecipes", mockRecipes.filter(mockRecipeFilter));
      } else {
        emit("update:filteredRecipes", filteredRecipes);
      }
    } catch (error) {
      console.error("Fehler beim Abrufen der Rezepte. Verwende Mockrezepte:", error);
      emit("update:filteredRecipes", mockRecipes.filter(mockRecipeFilter));
    }
  } else {
    console.log("Keine Suchanfrage. Zeige alle Mockrezepte.");
    emit("update:filteredRecipes", mockRecipes);
  }
}

function mockRecipeFilter(recipe) {
  return recipe.name.toLowerCase().includes(searchQuery.value.trim().toLowerCase());
}
</script>

<style scoped>
.search-bar {
  display: flex;
  align-items: center;
  background-color: rgba(240, 245, 255, 1);
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
