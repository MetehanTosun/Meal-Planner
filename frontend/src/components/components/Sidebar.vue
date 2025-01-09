<template>
  <div class="sidebar">
    <div class="sidebar-header">
      <p>Recipes</p>
    </div>

    <SearchbarComponent @update:search="applySearchQuery" />

    <div class="diet-filter">
      <p>Filter:</p>
      <label>
        <input
          type="checkbox"
          value="VEGETARIAN"
          v-model="dietFilter"
          @change="applyDietFilter"
        />
        Vegetarisch
      </label>
      <label>
        <input
          type="checkbox"
          value="VEGAN"
          v-model="dietFilter"
          @change="applyDietFilter"
        />
        Vegan
      </label>
    </div>

    <div class="create-recipe">
      <button class="create-recipe-button" @click="openCreateRecipe">
        + Neues Rezept
      </button>
    </div>

    <ul class="recipe-list">
      <li
        v-for="recipe in filteredRecipes"
        :key="recipe.id"
        :draggable="true"
        @dragstart="dragStart($event, recipe)"
        @dragend="dragEnd"
      >
        <p>{{ recipe.name }}</p>
      </li>
    </ul>

    <CreateRecipeView
      ref="createRecipeModal"
      @recipe-created="handleRecipeCreated"
    />
  </div>
</template>


<script setup>
/*
  * Comments may not be 100% accurate, since they were generated with ChatGPT.
  * The comments should be adjusted if needed. (Ethan Banovic)
 */
import { ref, computed, onMounted } from 'vue';
import SearchbarComponent from './SearchbarComponent.vue';
import CreateRecipeView from './CreateRecipeView.vue';
import axios from '@/axios';

const recipes = ref([]); // All recipes fetched from the backend
const searchQuery = ref(''); // Current search query entered by the user
const dietFilter = ref([]); // Array to store the active diet filters (e.g., ["VEGETARIAN", "VEGAN"])
const draggedItem = ref(null); // Currently dragged recipe (for drag-and-drop functionality)
const createRecipeModal = ref(false); // Modal state for creating new recipes

/**
 * Computed property to filter recipes based on dietFilter and searchQuery.
 * - Filters recipes by diet type (e.g., "VEGETARIAN", "VEGAN").
 * - Applies search query to match recipe names.
 */
const filteredRecipes = computed(() => {
  let result = recipes.value;

  if (dietFilter.value.length) {
    result = result.filter((recipe) => recipe.foodType === dietFilter.value[0]);
  }

  if (searchQuery.value.trim()) {
    result = result.filter((recipe) =>
      recipe.name.toLowerCase().includes(searchQuery.value.trim().toLowerCase())
    );
  }

  return result;
});

/**
 * Fetch recipes from the backend API.
 * - Retrieves all recipes and assigns them to the `recipes` state.
 * - Logs an error if the fetch fails.
 */
const fetchRecipes = async () => {
  try {
    const response = await axios.get('/recipes');
    recipes.value = response.data;
  } catch (error) {
    console.error('Error fetching recipes:', error);
    recipes.value = [];
  }
};

/**
 * Apply the search query to filter recipes.
 * - Updates the `searchQuery` state.
 * - This function is called when the search query changes.
 * @param {string} query - The search query entered by the user.
 */
const applySearchQuery = (query) => {
  searchQuery.value = query;
};

/**
 * Apply the selected diet filter.
 * - Ensures only one diet filter is active at a time.
 */
const applyDietFilter = () => {
  if (dietFilter.value.length > 1) {
    dietFilter.value = [dietFilter.value[dietFilter.value.length - 1]]; // Keep only the last applied filter
  }
};

/**
 * Handle the drag-and-drop functionality.
 * - Called when a recipe starts being dragged.
 * @param {DragEvent} event - The drag event object.
 * @param {Object} recipe - The recipe being dragged.
 */
const dragStart = (event, recipe) => {
  draggedItem.value = recipe;
  const dragData = {
    id: recipe.id,
    name: recipe.name,
    time: recipe.time,
    foodType: recipe.foodType,
    ingredients: recipe.ingredients || [],
  };
  event.dataTransfer.setData('application/json', JSON.stringify(dragData));
  console.log('Drag started:', dragData);
};

/**
 * Clear the `draggedItem` state after dragging ends.
 */
const dragEnd = () => {
  console.log('Drag ended:', draggedItem.value);
  draggedItem.value = null;
};

/**
 * Open the recipe creation modal.
 * - Sets the modal's `showModal` property to true.
 */
const openCreateRecipe = () => {
  createRecipeModal.value.showModal = true;
};

/**
 * Handle the creation of a new recipe.
 * - Fetches the updated list of recipes after a recipe is created.
 */
const handleRecipeCreated = () => {
  console.log('Recipe created, refreshing recipes...');
  fetchRecipes();
};

// Fetch recipes when the component is mounted
onMounted(fetchRecipes);
</script>

<style scoped>
.sidebar {
  width: 300px;
  background-color: rgba(25, 30, 40, 0.95);
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.sidebar-header {
  font-weight: bold;
  font-size: 20px;
  text-align: center;
  color: white;
  margin-bottom: 15px;
  text-transform: uppercase;
}

.diet-filter {
  margin-top: 20px;
  margin-bottom: 20px;
  padding: 10px;
  background-color: rgba(30, 35, 45, 0.9);
  border-radius: 6px;
  color: white;
}

.diet-filter label {
  display: block;
  margin-bottom: 10px;
  font-size: 14px;
}

.diet-filter input[type="checkbox"] {
  margin-right: 8px;
}

.create-recipe-button {
  display: block;
  width: 100%;
  padding: 10px;
  margin: 20px 0;
  font-size: 16px;
  color: white;
  background-color: #4CAF50;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.create-recipe-button:hover {
  background-color: #45a049;
}

.recipe-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.recipe-list li {
  margin: 10px 0;
  padding: 10px;
  background-color: rgba(40, 45, 55, 0.8);
  border-radius: 6px;
  color: white;
  text-align: center;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

.recipe-list li:hover {
  background-color: rgba(70, 130, 180, 0.3);
  transform: scale(1.02);
}
</style>
