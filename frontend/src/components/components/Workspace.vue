<template>
  <div class="day-container">
    <div class="menu-bar">
      <div class="menu-item" v-for="(day, index) in days" :key="index">
        <p class="item-label">{{ day }}</p>
        <div
          class="drop-zone"
          :dropzone="true"
          :day="day"
          @dragover.prevent
          @drop.prevent
          @drop="addRecipe($event, day)"
        >
          <ul class="recipe-day-list">
            <li v-for="(recipe, index) in weeklyList[day]" :key="index">
              <p>{{ recipe.name }}</p>
              <button @click="deleteRecipe(day, index)" class="delete-recipe-button">
                x
              </button>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/axios'
import { getUserId } from '@/storage/userStorage'
import router from '@/router';
import { weeklyList } from '@/classes/weeklyList';


const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']

const clearWeeklyList = () => {
  Object.keys(weeklyList.value).forEach(day => {
    weeklyList.value[day] = []
  })
}

const fetchOrCreateWeek = async () => {
  try {
    const userId = getUserId()
    if (!userId) {
      router.push('/login')
      return
    }

    console.log('Fetching week for user:', userId)
    const response = await axios.get(`/weeks/current/${userId}`)
    const weekData = response.data

    console.log('Received week data:', weekData)
    clearWeeklyList()

    if (weekData.days) {
      weekData.days.forEach((day) => {
        if (day.date) {
          const date = new Date(day.date)
          const dayName = date.toLocaleDateString('en-US', { weekday: 'long' })
          if (weeklyList.value[dayName]) {
            weeklyList.value[dayName] = day.recipes || []
          }
        }
      })
    }
  } catch (error) {
    console.error('Error fetching week:', error)
  }
}

const getDayId = async (dayName) => {
  try {
    const response = await axios.get(`/days/byName/${dayName}`);
    return response.data.id;
  } catch (error) {
    console.error(`Error getting day ID for ${dayName}:`, error);
    throw error;
  }
};

const addRecipe = async (event, dayName) => {
  try {
    if (!event.dataTransfer || !event.dataTransfer.getData) {
      console.error("No valid dataTransfer object found in event.");
      return;
    }

    const recipeData = event.dataTransfer.getData("application/json");
    if (!recipeData) {
      console.error("No recipe data found in event.");
      return;
    }

    const recipe = JSON.parse(recipeData);
    console.log("Recipe to add:", recipe);

    const dayId = await getDayId(dayName);
    console.log(`Got day ID ${dayId} for ${dayName}`);

    if (!weeklyList.value[dayName]) {
      console.error(`No list found for day ${dayName}`);
      return;
    }

    const response = await axios.post(`/days/${dayId}/add-recipe`, recipe);

    if (response.data) {
      weeklyList.value[dayName].push(recipe);
      console.log(`Recipe added to ${dayName} (ID: ${dayId}) on the server.`);
    }
  } catch (error) {
    console.error("Error adding recipe:", error.response?.data || error.message || error);
  }
};

const deleteRecipe = async (day, index) => {
  try {
    const recipe = weeklyList.value[day][index];
    if (!recipe || !recipe.id) {
      console.error('No valid recipe found at index', index);
      return;
    }

    const dayId = await getDayId(day);
    
    await axios.delete(`/days/${dayId}/remove-recipe/${recipe.id}`);
    
    weeklyList.value[day].splice(index, 1);
    
    console.log(`Recipe removed from ${day}`);
  } catch (error) {
    console.error("Error removing recipe:", error.response?.data || error.message || error);
  }
};

onMounted(() => {
  if (!getUserId()) {
    router.push('/login')
    return
  }
  fetchOrCreateWeek()
})
</script>

<style>
.day-container {
  padding: 20px 20px 20px 20px;
  background-color: #181818;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  height: 100vh;
  width: 100vw;
}

.menu-bar {
  display: flex;
  flex-direction: row;
  width: 100%;
  gap: 15px;
  justify-content: space-between;
}

.day-label {
  align-self: flex-start;
  font-weight: bold;
  font-size: 30px;
  margin-bottom: 10px;
}

.drop-zone {
  display: flex;
  align-content: center;
  flex: 1;
  width: 100%;
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 8px;
  margin-top: 10px;
  overflow-y: auto;
}

.menu-item {
  flex-grow: 1;
  flex-grow: 0;
  min-width: 155px;
  padding: 5px 5px 5px 5px;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 18px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2);
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
  position: relative;
  height: 500px;
}

.menu-item:nth-child(1) {
  background-color: #1e90ff;
}

.menu-item:nth-child(2) {
  background-color: #87ceeb;
}

.menu-item:nth-child(3) {
  background-color: #8a2be2;
}

.menu-item:nth-child(4) {
  background-color: #7d3c98;
}

.menu-item:nth-child(5) {
  background-color: #4b0082;
}

.menu-item:nth-child(6) {
  background-color: #40e0d0;
}

.menu-item:nth-child(7) {
  background-color: #6a5acd;
}

.menu-item:hover {
  transform: scale(1.05);
  box-shadow: 0px 6px 12px rgba(0, 0, 0, 0.4);
}

.menu-item p {
  margin: 0;
}

.recipe-day-list {
  padding: 5px 4%;
  height: 100%;
  width: 100%;
  flex: display;
  align-content: top;
  justify-content: center;
  list-style-type: none;
}

.recipe-day-list li {
  font-size: 14px;
  margin: 4px 0;
  padding: 4px 4px;
  cursor: pointer;
  color: white;
  background-color: rgba(0, 0, 0, 0.3);
  border-radius: 6px;
  text-align: center;
  transition:
    background-color 0.3s ease,
    transform 0.2s ease;
}

.recipe-day-list li:hover {
  background-color: rgba(0, 0, 0, 0.4);
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.3);
}

.delete-recipe-button {
  background-color: rgb(238, 29, 29);
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 4px;
  font-size: 12px;
  width: 100%;
  height: 10px;
  padding: 0;
  line-height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.3s ease;
}
</style>