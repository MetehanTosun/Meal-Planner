<template>
  <div class="sidebar">
    <!-- Fixed content -->
    <div class="sidebar-fixed">
      <!-- Header -->
      <div class="sidebar-header">
        <p>Rezepte</p>
      </div>
 
      <!-- Search bar -->
      <SearchbarComponent @update:search="applySearchQuery" />
 
      <!-- Dietary preference filters -->
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
        <label>
          <input
            type="checkbox"
            value="MEAT"
            v-model="dietFilter"
            @change="applyDietFilter"
          />
          Fleisch
        </label>
      </div>
 
      <!-- Action buttons for creating recipes and showing favorites -->
      <div class="action-buttons">
        <button class="create-recipe-button" @click="openCreateRecipe">
          + Neues Rezept
        </button>
        <button
          class="favorites-button"
          @click="toggleFavoritesFilter"
          :class="{ active: showOnlyFavorites }"
        >
          <span class="star-icon">★</span> Favoriten anzeigen
        </button>
      </div>
    </div>

    <!-- Scrollable recipe list -->
    <div class="recipe-list-container">
      <ul class="recipe-list">
        <li
          v-for="recipe in filteredRecipes"
          :key="recipe.id"
          :draggable="true"
          @dragstart="dragStart($event, recipe)"
          @dragend="dragEnd"
        >
          <div class="recipe-item">
            <div class="recipe-info">
              <button
                class="favorite-toggle"
                @click="toggleFavorite(recipe)"
                :class="{ 'is-favorite': recipe.isFavorite }"
              >
                ★
              </button>
              <p>{{ recipe.name }}</p>
            </div>
            <div class="recipe-actions">
              <button class="share-button" @click.stop="openShareModal(recipe.id)">
                Teilen
              </button>
              <button class="delete-button" @click.stop="deleteRecipe(recipe)">
                ✕
              </button>
            </div>
          </div>
        </li>
      </ul>
    </div>
 
    <!-- Modal components -->
    <CreateRecipeView
      ref="createRecipeModal"
      @recipe-created="handleRecipeCreated"
    />
    <ShareRecipeModal ref="shareRecipeModal" />
    <DeleteConfirmationModal ref="deleteConfirmationModal" @confirm="handleDeleteConfirm" />
  </div>
</template>

<script setup>
  import { ref, computed, onMounted } from 'vue';
  import SearchbarComponent from './SearchbarComponent.vue';
  import CreateRecipeView from './CreateRecipeView.vue';
  import ShareRecipeModal from './ShareRecipeModal.vue';
  import axios from '@/axios';
  import DeleteConfirmationModal from './DeleteConfirmationModal.vue';
  import { useWeekStore } from '../../state-management/index.js';

  // State
  const recipes = ref([]); // All recipes fetched from the backend
  const searchQuery = ref(''); // Current search query entered by the user
  const dietFilter = ref([]); // Array to store the active diet filters (e.g., ["VEGETARIAN", "VEGAN"])
  const draggedItem = ref(null); // Currently dragged recipe (for drag-and-drop functionality)
  const createRecipeModal = ref(null); // Reference to create recipe modal
  const shareRecipeModal = ref(null); // Reference to share recipe modal
  const showOnlyFavorites = ref(false); // Toggle for favorites filter
  const deleteConfirmationModal = ref(null); // Reference to the delete confirmation modal component
  const weekStore = useWeekStore();

  /**
   * Computed property to filter recipes based on dietFilter, searchQuery, and favorites.
   */
  const filteredRecipes = computed(() => {
    let result = recipes.value;

    // Apply favorites filter if enabled
    if (showOnlyFavorites.value) {
      result = result.filter(recipe => recipe.isFavorite);
    }

    // Apply diet filter if set
    if (dietFilter.value.length) {
      result = result.filter((recipe) => recipe.foodType === dietFilter.value[0]);
    }

    // Apply search filter if query exists
    if (searchQuery.value.trim()) {
      result = result.filter((recipe) =>
        recipe.name.toLowerCase().includes(searchQuery.value.trim().toLowerCase())
      );
    }

    return result;
  });

  /**
   * Fetches all recipes from the backend API and processes their favorite status.
   * - Retrieves the current user's ID from local storage
   * - Makes an API call to get all recipes
   * - Maps through the received recipes to add a favorite status flag
   * - Updates the reactive recipes array with the processed data
   * - Sets empty array if the fetch fails
   */
  const fetchRecipes = async () => {
    try {
      const userId = localStorage.getItem('userId');
      const response = await axios.get('/recipes');
      recipes.value = response.data.map(recipe => ({
        ...recipe,
        isFavorite: recipe.favoriteByUsers.includes(parseInt(userId))
      }));
    } catch (error) {
      console.error('Error fetching recipes:', error);
      recipes.value = [];
    }
  };

  /**
   * Toggle the favorites filter
   */
  const toggleFavoritesFilter = () => {
    showOnlyFavorites.value = !showOnlyFavorites.value;
  };

  /**
   * Toggles the favorite status of a specific recipe for the current user.
   * - Gets the user ID from local storage
   * - Makes an API call to toggle the favorite status
   * - Updates the local favorite status of the recipe on success
   * - Shows an error alert if the toggle operation fails
   *
   * @param {Object} recipe - The recipe object to toggle favorite status for
   */
  const toggleFavorite = async (recipe) => {
    try {
      console.log('Toggling favorite for recipe:', recipe.id);
      const response = await axios.put(`/recipes/${recipe.id}/toggle-favorite`);
      console.log('Favorite toggled successfully:', response.data);
      recipe.isFavorite = !recipe.isFavorite;
    } catch (error) {
      console.error('Toggle favorite error:', error);

      // Prüfe auf spezifische Fehlercodes
      if (error.response?.status === 404) {
        alert('Benutzer wurde nicht gefunden.');
        return;
      }
      if (error.response?.status === 401) {
        alert('Bitte melden Sie sich erneut an.');
        return;
      }

      const errorMsg = error.response?.data?.message || error.message || 'Ein Fehler ist aufgetreten';
      alert(errorMsg);
    }
  };

  /**
   * Apply the search query to filter recipes
   */
  const applySearchQuery = (query) => {
    searchQuery.value = query;
  };

  /**
   * Apply the diet filter
   */
  const applyDietFilter = () => {
    if (dietFilter.value.length > 1) {
      dietFilter.value = [dietFilter.value[dietFilter.value.length - 1]];
    }
  };

  /**
   * Handle drag start event
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
   * Handle drag end event
   */
  const dragEnd = () => {
    console.log('Drag ended:', draggedItem.value);
    draggedItem.value = null;
  };

  /**
   * Open create recipe modal
   */
  const openCreateRecipe = () => {
    createRecipeModal.value.showModal = true;
  };

  /**
   * Handle recipe created event
   */
  const handleRecipeCreated = () => {
    console.log('Recipe created, refreshing recipes...');
    fetchRecipes();
  };

  /**
   * Open share modal for a recipe
   */
  const openShareModal = (recipeId) => {
    shareRecipeModal.value.openModal(recipeId);
  };


  /**
  * Opens the confirmation modal for recipe deletion
  */
  const deleteRecipe = (recipe) => {
    deleteConfirmationModal.value.openModal(recipe);
  };

  /**
   * Handles the confirmation of recipe deletion
   */
  const handleDeleteConfirm = async (recipe) => {
    try {
      await axios.delete(`/recipes/${recipe.id}`);
      await weekStore.removeRecipeFromAllDays(recipe.id);
      await fetchRecipes(); // Refresh recipe list after successful deletion
    } catch (error) {
      console.error('Error deleting recipe:', error);
      alert('Error deleting recipe. Please try again.');
    }
  };

  // Initialize
  onMounted(fetchRecipes);
</script>

<style scoped>
.sidebar {
  width: 300px;
  background-color: #1f1f1f;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-fixed {
  flex-shrink: 0;
}

.recipe-list-container {
  flex-grow: 1;
  overflow-y: auto;
  margin-top: 20px;
  scrollbar-width: thin;
  scrollbar-color: #4a4a4a #1f1f1f;
}

.recipe-list-container::-webkit-scrollbar {
  width: 8px;
}

.recipe-list-container::-webkit-scrollbar-track {
  background: #1f1f1f;
}

.recipe-list-container::-webkit-scrollbar-thumb {
  background-color: #4a4a4a;
  border-radius: 4px;
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
  background-color: #181818;
  border-radius: 6px;
  border: 1px solid #fff;
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

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin: 20px 0;
}

.create-recipe-button {
  width: 100%;
  padding: 10px;
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

.favorites-button {
  width: 100%;
  padding: 10px;
  font-size: 16px;
  color: white;
  background-color: #FFA000;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.favorites-button:hover {
  background-color: #FF8F00;
}

.favorites-button.active {
  background-color: #FF6F00;
}

.star-icon {
  font-size: 18px;
}

.recipe-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.recipe-list li {
  margin: 10px 0;
  padding: 10px;
  background-color: #181818;
  border-radius: 6px;
  border: 1px solid #fff;
  color: white;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

.recipe-list li:hover {
  background-color: rgba(24, 24, 24, 0.1);
  transform: scale(1.02);
}

.recipe-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  width: 100%;
}

.recipe-info {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  flex: 1;
}

.recipe-info p {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin: 0;
  min-width: 0;
}

.recipe-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.favorite-toggle {
  background: none;
  border: none;
  color: #ffffff;
  font-size: 20px;
  cursor: pointer;
  padding: 0;
  opacity: 0.5;
  transition: opacity 0.2s ease;
  flex-shrink: 0;
}

.favorite-toggle:hover {
  opacity: 0.8;
}

.favorite-toggle.is-favorite {
  opacity: 1;
  color: #FFB300;
}

.share-button {
  background-color: #2196F3;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 4px 8px;
  cursor: pointer;
  font-size: 12px;
  transition: background-color 0.2s ease;
}

.share-button:hover {
  background-color: #1976D2;
}

.delete-button {
  background-color: #000000;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 4px 8px;
  cursor: pointer;
  font-size: 12px;
  transition: background-color 0.2s ease;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.delete-button:hover {
  background-color: #c82333;
}
</style>
