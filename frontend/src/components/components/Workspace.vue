<template>
  <div class="day-container">
    <div class="interactive-container">
      <button
        class="custom-arrow-button left"
        aria-label="Previous week"
        @click="weekStore.moveToPreviousWeek"
        :disabled="weekStore.currentWeekIndex === 0"
      >
        &#9664;
      </button>
      <div class="menu-bar">
        <h2>Rezepte der Woche</h2>
      </div>
      <button
        class="custom-arrow-button right"
        aria-label="Next week"
        @click="weekStore.moveToNextWeek"
        :disabled="weekStore.currentWeekIndex === weekStore.weeks.length - 1"
      >
        &#9654;
      </button>
    </div>

    <div class="menu-bar">
      <div
        class="menu-item"
        v-for="day in weekStore.currentDays"
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
              v-for="recipe in weekStore.getRecipesForDay(day.id)"
              :key="recipe.id"
            >
              <p>{{ recipe.recipeData.name || 'Recipe Placeholder' }}</p>
              <div class="action-buttons">
            <span
              class="portionsDisplay"
              @click="decrementPortions(recipe)"
            >
              {{ recipe.portions }}
            </span>
                <button
                  class="informationTab"
                  @click="openRecipeInfo(recipe)"
                >
                  <p>ℹ</p>
                </button>
                <button
                  @click="removeRecipe(day.id, recipe.recipeData.id)"
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
    <ReactiveDashboard />
    <RecipeDataPopup
      v-if="showPopup"
      :recipe="selectedRecipe"
      :show="showPopup"
      @close="closePopup"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useWeekStore } from '@/state-management/index.js';
import ReactiveDashboard from '@/components/components/Dashboards/ReactiveDashboardPlanner.vue';
import RecipeDataPopup from '@/components/components/RecipeDataPopup.vue'

const weekStore = useWeekStore();
const selectedRecipe = ref(null);

const showPopup = ref(false);

const handleDrop = async (event, dayId) => {
  const recipeData = event.dataTransfer.getData('application/json');
  if (recipeData) {
    const recipe = JSON.parse(recipeData);
    try {
      const day = weekStore.currentDays.find((d) => d.id === dayId);

      if (!day) {
        console.error(`Day with ID ${dayId} not found.`);
        return;
      }

      const existingRecipe = day.userSpecificRecipes.find(
        (r) => r.recipeData.id === recipe.id
      );

      if (existingRecipe) {
        await weekStore.incrementRecipePortions(existingRecipe.id);
        existingRecipe.portions += 1;
      } else {
        await weekStore.addRecipeToDay(dayId, recipe.id, 1);
      }
    } catch (error) {
      console.error('Error adding/updating recipe:', error);
    }
  }
};

const removeRecipe = async (dayId, recipeId) => {
  try {
    await weekStore.removeRecipeFromDay(dayId, recipeId);
  } catch (error) {
    console.error('Error removing recipe:', error);
  }
};

const decrementPortions = async (recipe) => {
  try {
    await weekStore.decrementRecipePortions(recipe.id);
  } catch (error) {
    console.error('Error decrementing portions:', error);
  }
};

onMounted(async () => {
  await weekStore.fetchWeeksInRange(2);
  await weekStore.handleWeekTransition();
});

const closePopup = () => {
  showPopup.value = false;
  selectedRecipe.value = null;
};
const openRecipeInfo = (userSpecificRecipe) => {
  selectedRecipe.value = userSpecificRecipe;
  console.log('Currently this USR is selected',userSpecificRecipe.id);
  console.log(userSpecificRecipe.recipeData.foodType);
  showPopup.value = true;
};
</script>


<style>
.day-container {
  height: 100%;
  width: 100%;
  box-sizing: border-box;
}

.date-label {
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

.menu-item {
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

.recipe-day-list li {
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
  gap: 0.3rem;
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
  box-shadow:
    inset 1px 1px 2px rgba(0, 0, 0, 0.5),
    inset -1px -1px 2px rgba(255, 255, 255, 0.08);
  transition:
    background-color 0.3s,
    box-shadow 0.3s;
}

.informationTab:hover {
  background-color: #2e7dcc;
  box-shadow:
    inset 2px 2px 3px rgba(5, 159, 255, 0.9),
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
  box-shadow:
    inset 1px 1px 2px rgba(0, 0, 0, 0.5),
    inset -1px -1px 2px rgba(255, 255, 255, 0.08);
  transition:
    background-color 0.3s,
    box-shadow 0.3s;
}

.delete-recipe-button:hover {
  background-color: #191919;
  box-shadow:
    inset 2px 2px 3px rgba(0, 0, 0, 0.4),
    inset -2px -2px 3px rgba(30, 30, 30, 0.1);
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
  box-shadow:
    inset 1px 1px 2px rgba(0, 0, 0, 0.5),
    inset -1px -1px 2px rgba(255, 255, 255, 0.08);
  transition:
    background-color 0.3s,
    box-shadow 0.3s;
}

.portionsDisplay:hover {
  background-color: #cc2e2e;
  box-shadow:
    inset 2px 2px 3px rgba(255, 5, 5, 0.9),
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
  background-color: white;
  color: #1f1f1f;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 1.2rem;
  transition:
    background-color 0.3s ease,
    transform 0.2s ease;
}

.custom-arrow-button:hover {
  background-color: #1f1f1f;
  color: white;
  transform: scale(1.1);
}

.custom-arrow-button.left {
  margin-right: auto;
}

.custom-arrow-button.right {
  margin-left: auto;
}

.recipe-week-list li {
  padding: 5px;
  background-color: #444;
  color: white;
  border-radius: 4px;
  margin-bottom: 5px;
}

</style>
