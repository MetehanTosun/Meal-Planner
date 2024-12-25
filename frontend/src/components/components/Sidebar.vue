<template>
  <div class="sidebar">
    <div class="sidebar-header"><p>Recipes</p></div>

    <SearchbarComponent @update:filtered-recipes="applyDietFilter" />

    <div class="diet-filter">
      <p>Filter:</p>
      <label>
        <input type="checkbox" value="VEGETARIAN" v-model="dietFilter" @change="handleDietChange" />
        Vegetarisch
      </label>
      <label>
        <input type="checkbox" value="VEGAN" v-model="dietFilter" @change="handleDietChange" />
        Vegan
      </label>
    </div>

    <div class="create-recipe">
      <button class="create-recipe-button" @click="openCreateRecipe">
        + Neues Rezept
      </button>
    </div>

    <!-- Button für die Einkaufsliste -->
    <div class="generate-shopping-list">
      <button class="shopping-list-button" @click="openModal">
        Einkaufsliste generieren
      </button>
    </div>

    <ul class="recipe-list">
      <li v-for="item in filteredRecipes"
          :key="item.id"
          :draggable="true"
          @dragstart="dragStart($event, item)"
          @dragend="dragEnd()">
        <p>{{ item.name }}</p>
      </li>
    </ul>

    <CreateRecipeView
      ref="createRecipeModal"
      @recipe-created="handleRecipeCreated"
    />

    <!-- Modal für die Einkaufsliste -->
<div v-if="isModalOpen" class="modal-overlay">
  <div class="modal-content">
    <h2>Einkaufsliste</h2>
    <div v-if="Object.keys(shoppingList).length > 0">
      <ul>
        <li v-for="(amount, ingredient) in shoppingList" :key="ingredient">
          {{ ingredient }}: {{ amount }}
        </li>
      </ul>
    </div>
    <div v-else>
      <p>Die Liste ist leer.</p>
    </div>
    <button class="close-btn" @click="closeModal">Schließen</button>
  </div>
</div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from '@/axios';
import SearchbarComponent from './SearchbarComponent.vue';
import CreateRecipeView from './CreateRecipeView.vue';

const recipes = ref([]);
const filteredRecipes = ref([]);
const draggedItem = ref(null);
const dietFilter = ref([]);
const createRecipeModal = ref(null);

// Einkaufsliste
const shoppingList = ref([]);
const isModalOpen = ref(false);

const fetchRecipes = async () => {
  try {
    const response = await axios.get('/recipes');
    recipes.value = response.data;
    applyDietFilter(recipes.value);
  } catch (error) {
    console.error('Error fetching recipes:', error);
    recipes.value = [];
  }
};

const dragStart = (event, item) => {
  draggedItem.value = item;
  const dragData = { ...item };
  event.dataTransfer.setData("application/json", JSON.stringify(dragData));
};

const dragEnd = () => {
  draggedItem.value = null;
};

const handleDietChange = () => {
  if (dietFilter.value.length > 1) {
    dietFilter.value = [dietFilter.value[dietFilter.value.length - 1]];
  }
  applyDietFilter(recipes.value);
};

const applyDietFilter = (recipeList = []) => {
  const currentFilter = dietFilter.value[0];
  filteredRecipes.value = !currentFilter
    ? recipeList
    : recipeList.filter(recipe => recipe.foodtype === currentFilter);
};

const openCreateRecipe = () => {
  if (createRecipeModal.value) {
    createRecipeModal.value.showModal = true;
  }
};

const handleRecipeCreated = () => {
  fetchRecipes();
};

// Funktionen für die Einkaufsliste
const openModal = async () => {
  await generateShoppingList();
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
};

const generateShoppingList = async () => {
  try {
    // POST-Anfrage mit Axios
    const response = await axios.post('/shopping-list', recipes.value);
    
    // Antwort-Daten direkt auslesen
    shoppingList.value = response.data;

    // Debugging: Einkaufsliste und Antwort loggen
    console.log('Einkaufsliste:', shoppingList.value);
    console.log('Vollständige Antwort:', response);
  } catch (error) {
    console.error('Fehler beim Generieren der Einkaufsliste:', error);
  }
};


// Initialisierung
onMounted(() => {
  fetchRecipes();
});
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

.shopping-list-button {
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

.shopping-list-button:hover {
  background-color: #45a049;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000; /* Sehr hoher Wert, um über allen anderen Elementen zu liegen */
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
  text-align: center;
  z-index: 1000; /* Sehr hoher Wert, um über allen anderen Elementen zu liegen */
}

.close-btn {
  margin-top: 20px;
  background: #f44336;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;
}

.close-btn:hover {
  background: #d32f2f;
}
</style>
