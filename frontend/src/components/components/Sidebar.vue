<template>
  <div class="sidebar">
    <div class="sidebar-header"><p>Recipes</p></div>

    <!-- Searchbar -->
    <SearchbarComponent @update:filtered-recipes="applyDietFilter" />

    <!-- Diet Filter Section -->
    <div class="diet-filter">
      <p>Filter:</p>
      <label>
        <input type="checkbox" value="vegetarian" v-model="dietFilter" @change="handleDietChange" />
        Vegetarisch
      </label>
      <label>
        <input type="checkbox" value="vegan" v-model="dietFilter" @change="handleDietChange" />
        Vegan
      </label>
    </div>

    <div class="create-recipe">
      <button class="create-recipe-button" @click="openCreateRecipe">
        + Neues Rezept
      </button>
    </div>

    <!-- Recipe List -->
    <ul class="recipe-list">
      <li v-for="(item, index) in filteredRecipes" :key="index" :draggable="true" @dragstart="dragStart($event, item)" @dragend="dragEnd()">
        <p>{{ item.name }}</p>
      </li>
    </ul>

    <!-- Add CreateRecipeView component here -->
    <CreateRecipeView ref="createRecipeModal" />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import SearchbarComponent from './SearchbarComponent.vue';
import { mockRecipes } from '@/classes/MockRecipe.js';
import CreateRecipeView from './CreateRecipeView.vue';

// Props
defineProps({
  filteredRecipes: {
    type: Array,
    required: true
  }
});

// Emits
const emit = defineEmits(['update:filtered-recipes']);

// Reactive variables
const draggedItem = ref(null);
const dietFilter = ref([]);
const createRecipeModal = ref(null);

// Methods
const dragStart = (event, item) => {
  if (draggedItem.value === null) {
    draggedItem.value = item;
    event.dataTransfer.setData("application/json", JSON.stringify(item));
    console.log("Currently Dragged Item:", JSON.stringify(item, null, 2));
  }
};

const dragEnd = () => {
  draggedItem.value = null;
};

// Handles changes in the diet filter
const handleDietChange = () => {
  // Only one checkbox can be selected at a time
  if (dietFilter.value.length > 1) {
    dietFilter.value = [dietFilter.value[dietFilter.value.length - 1]];
  }

  // Trigger filtering based on diet
  applyDietFilter();
};

const applyDietFilter = (recipes = []) => {
  const sourceRecipes = recipes.length > 0 ? recipes : mockRecipes;
  const currentFilter = dietFilter.value[0];
  const filtered = !currentFilter
    ? sourceRecipes
    : sourceRecipes.filter(recipe => recipe.diet.includes(currentFilter));

  emit('update:filtered-recipes', filtered);
};

// For CreateRecipeView
const openCreateRecipe = () => {
  // Check if modal exists before trying to access it
  if (createRecipeModal.value) {
    createRecipeModal.value.showModal = true;
  }
};
</script>

<style scoped>
.sidebar {
  width: 250px;
  background-color: rgba(25, 30, 40, 0.95);
  padding: 20px;
  box-sizing: border-box;
  border-radius: 8px;
  box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.2);
}

.sidebar-header {
  font-weight: bold;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
  background-color: rgba(70, 130, 180, 0.9);
  color: white;
  border-radius: 6px;
  margin-bottom: 15px;
  text-transform: uppercase;
}

.recipe-list {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.recipe-list li {
  margin: 10px 0;
  padding: 8px 12px;
  cursor: pointer;
  color: rgba(200, 200, 200, 1);
  background-color: rgba(40, 45, 55, 0.8);
  border-radius: 6px;
  text-align: center;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

.recipe-list li:hover {
  background-color: rgba(70, 130, 180, 0.3);
  transform: scale(1.02);
}

.diet-filter {
  margin: 15px 0;
  padding: 10px;
  background-color: rgba(30, 35, 45, 0.9);
  border-radius: 6px;
  color: white;
  font-size: 14px;
}

.diet-filter label {
  display: block;
  margin-bottom: 10px;
}

.diet-filter input[type="checkbox"] {
  margin-right: 8px;
}

.create-recipe-button {
  width: 100%;
  padding: 0.75rem;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin: 1rem 0;
  font-size: 1rem;
}

.create-recipe-button:hover {
  background-color: #45a049;
}
</style>
