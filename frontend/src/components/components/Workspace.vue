<template>
  <div class="day-container">
    <div class="interactive-container">
      <button
        class="custom-arrow-button left"
        aria-label="Previous week"
        @click="weekStore.goToPreviousWeek"
      >
        &#9664; <!-- Left arrow symbol -->
      </button>
      <div class="menu-bar">
        <!-- Your menu items -->
      </div>
      <button
        class="custom-arrow-button right"
        aria-label="Next week"
        @click="weekStore.goToNextWeek"
      >
        &#9654; <!-- Right arrow symbol -->
      </button>
    </div>
    <div class="menu-bar">
      <div
        class="menu-item"
        v-for="day in weekStore.days"
        :key="day.id"
      >
        <p class="item-label">
          {{ new Date(day.date).toLocaleDateString('de-DE', { weekday: 'long' }) }}
        </p>
        <p class="date-label">
          {{ new Date(day.date).toLocaleDateString('de-DE') }}
        </p>
        <div
          class="drop-zone"
          :dropzone="true"
          :day="day.id"
          @dragover.prevent
          @drop.prevent="handleDrop($event, day.id)"
        >
          <ul class="recipe-day-list">
            <li
              v-for="(userSpecificRecipe) in day.userSpecificRecipes"
              :key="userSpecificRecipe.id"
            >
              <p>{{ userSpecificRecipe.recipeData.name || 'Recipe Placeholder' }}</p>
              <div class="action-buttons">
                <span class="portionsDisplay" @click="decrementPortions(userSpecificRecipe)">{{ userSpecificRecipe.portions }}</span>
                <button class="informationTab" @click="toggleTab(userSpecificRecipe)">
                  <p>ℹ</p>
                </button>
                <div v-if="selectedRecipe === userSpecificRecipe" class="overlay" @click="closeTab">
                  <div @click.stop class="informationDisplay">
                    <h3>Recipe Information</h3>
                    <p><b>Name:</b> {{ selectedRecipe.recipeData.name }}</p>
                    <p><b>Minutes:</b> {{ selectedRecipe.recipeData.time }}</p>
                    <p><b>Foodtype:</b> {{ selectedRecipe.recipeData.foodtype }}</p>
                    <div>
                      <h4>Ingredients:</h4>
                      <p
                        v-for="(ingredient, index) in selectedRecipe.recipeData.ingredients"
                        :key="index"
                      >
                        <b>{{ ingredient.name }}</b> - {{ ingredient.amount }}{{ ingredient.unit }}
                      </p>
                    </div>
                  </div>
                </div>

                <button
                  @click="removeRecipe(day.id, userSpecificRecipe.recipeData.id)"
                  class="delete-recipe-button"
                >
                  x
                </button>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <ReactiveDashboard/>
  </div>
</template>



<script setup>
import { ref, onMounted } from 'vue';
import { useWeekStore } from '@/state-management/index.js';
import ReactiveDashboard from '@/components/components/ReactiveDashboard.vue'

const weekStore = useWeekStore();
const selectedRecipe = ref(null);

const toggleTab = (recipe) => {
  if (selectedRecipe.value === recipe) {
    selectedRecipe.value = null;
  } else {
    selectedRecipe.value = recipe;
  }
};

const closeTab = () => {
  selectedRecipe.value = null;
};

const handleDrop = async (event, dayId) => {
  const recipeData = event.dataTransfer.getData('application/json');
  if (recipeData) {
    const recipe = JSON.parse(recipeData);
    try {
      const day = weekStore.days.find((day) => day.id === dayId);

      if (!day) {
        console.error(`Day with ID ${dayId} not found.`);
        return;
      }

      // Check if the recipe already exists in the day's userSpecificRecipes
      const existingRecipe = day.userSpecificRecipes.find(
        (userSpecificRecipe) => userSpecificRecipe.recipeData.id === recipe.id
      );

      if (existingRecipe) {
        // Call the store action to increment portions
        await weekStore.incrementRecipePortions(existingRecipe.id);
        existingRecipe.portions += 1; // Optimistically update local state
      } else {
        // Add a new recipe if it doesn't exist
        await weekStore.addRecipeToDay(dayId, recipe.id, 1);
      }
    } catch (error) {
      console.error('Error adding/updating recipe:', error);
    }
  }
};

const removeRecipe = async (dayId, recipeId) => {
  try {
    const day = weekStore.days.find((day) => day.id === dayId);

    if (!day || !day.userSpecificRecipes) {
      console.error(`Day or recipe not found: dayId=${dayId}, recipeId=${recipeId}`);
      return;
    }

    const recipeIndex = day.userSpecificRecipes.findIndex(
      (recipe) => recipe.recipeData.id === recipeId
    );

    if (recipeIndex === -1) {
      console.error(`Recipe with id ${recipeId} not found in day with ID ${dayId}`);
      return;
    }

    console.log(`Removing recipe with ID ${recipeId} from day ID: ${dayId}`);

    await weekStore.removeRecipeFromDay(dayId, recipeId);
    day.userSpecificRecipes.splice(recipeIndex, 1);
  } catch (error) {
    console.error('Error removing recipe:', error);
  }
};
const decrementPortions = async (userSpecificRecipe) => {
  try {
    console.log('Decrement Portions');
    await weekStore.decrementRecipePortions(userSpecificRecipe.id);

    if (userSpecificRecipe.portions > 1) {
      userSpecificRecipe.portions -= 1;
    }
  } catch (error) {
    console.error('Error decrementing portions:', error);
  }
};

onMounted(() => {
  weekStore.fetchCurrentWeek();
});
</script>



<style>
.day-container {
  height: 100%;
  width: 100%;
  box-sizing: border-box;
}
.date-label{
  border-bottom: 1px solid #fff;
}
.interactive-container {
  height: 5%;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 1rem;
}
.menu-bar {
  max-height: 80%;
  height: 100%;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  padding-left: 2rem;
  padding-right: 2rem;
  color: white;
}
.menu-item{
  padding: 0.5rem;
  border: 1px solid white;
  border-radius: 8px;
  width: 12%;
  background-color: #1f1f1f;
  overflow: hidden;
}
.drop-zone {
  height: 95%;
  overflow-y: auto;
}
.recipe-day-list {
  list-style: none;
  padding-top: 1rem;
  padding-left: 0;
  margin: 0;
}
.recipe-day-list li{
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #333;
  color: white;
  border-radius: 4px;
  padding: 0.5rem;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
  position: relative;
  transition: background-color 0.3s ease;
}
.action-buttons {
  display: flex;
  align-items: center;
  gap: 0.3rem; /* Adjust spacing between buttons */
}
.informationTab {
  background-color: #272727;
  color: #b0b0b0;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
  cursor: pointer;
  box-shadow: inset 1px 1px 2px rgba(0, 0, 0, 0.5),
  inset -1px -1px 2px rgba(255, 255, 255, 0.08);
  transition: background-color 0.3s, box-shadow 0.3s;
}
.informationTab:hover {
  background-color: #2e7dcc;
  box-shadow: inset 2px 2px 3px rgba(5, 159, 255, 0.9),
  inset -2px -2px 3px rgba(41, 152, 193, 0.4);
  color: #fff;
}
.delete-recipe-button {
  background-color: #272727;
  color: #b0b0b0;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
  cursor: pointer;
  box-shadow: inset 1px 1px 2px rgba(0, 0, 0, 0.5),
  inset -1px -1px 2px rgba(255, 255, 255, 0.08);
  transition: background-color 0.3s, box-shadow 0.3s;
}
.delete-recipe-button:hover {
  background-color: #191919;
  box-shadow: inset 2px 2px 3px rgba(0, 0, 0, 0.4),
  inset -2px -2px 3px rgba(30, 30, 30, 0.1);
}
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5); /* Semi-transparent overlay */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}
.informationDisplay {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: rgba(90, 90, 90, 0.9);
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1rem;
  width: 300px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  opacity: 0.95;
}
.portionsDisplay {
  background-color: #272727;
  color: #b0b0b0;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
  cursor: pointer;
  box-shadow: inset 1px 1px 2px rgba(0, 0, 0, 0.5),
  inset -1px -1px 2px rgba(255, 255, 255, 0.08);
  transition: background-color 0.3s, box-shadow 0.3s;
}
.portionsDisplay:hover {
  background-color: #cc2e2e;
  box-shadow: inset 2px 2px 3px rgba(255, 5, 5, 0.9),
  inset -2px -2px 3px rgba(193, 41, 41, 0.4);
  color: #fff;
}

::-webkit-scrollbar {
  width: 10px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #888;
}

::-webkit-scrollbar-thumb:hover {
  background: #555;
}
.custom-arrow-button {
  background-color: white; /* Dark background */
  color: #1f1f1f; /* White arrow */
  border: none; /* No border */
  border-radius: 50%; /* Circular button */
  width: 40px; /* Button size */
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer; /* Pointer on hover */
  font-size: 1.2rem; /* Arrow size */
  transition: background-color 0.3s ease, transform 0.2s ease;
}

.custom-arrow-button:hover {
  background-color: #1f1f1f; /* Light background on hover */
  color: white; /* Dark arrow */
  transform: scale(1.1); /* Slight zoom effect */
}

.custom-arrow-button.left {
  margin-right: auto; /* Position on the left */
}

.custom-arrow-button.right {
  margin-left: auto; /* Position on the right */
}
</style>
