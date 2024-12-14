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
              <button @click="deleteRecipe(day, recipeIndex)" class="delete-recipe-button">
                x
              </button>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data() {
    return {
      days: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'],
      weeklyList: {
        Monday: [],
        Tuesday: [],
        Wednesday: [],
        Thursday: [],
        Friday: [],
        Saturday: [],
        Sunday: [],
      },
    }
  },
  methods: {
    // 11 Dec
    async fetchOrCreateWeek() {
      try {
        const userId = localStorage.getItem('userId')
        if (!userId) {
          console.error('User ID is not found in local storage')
          return
        }

        // Fetch the current week or create a new one
        const response = await axios.get(`http://localhost:8080/weeks/current/${userId}`)
        const weekData = response.data

        // Clear existing weeklyList
        this.clearWeeklyList()

        // Populate weeklyList with days from the fetched week
        if (weekData.days) {
          weekData.days.forEach((day) => {
            const dayName = new Date(day.date).toLocaleDateString('en-US', { weekday: 'long' })
            this.weeklyList[dayName].push(...day.recipes)
          })
        }
        console.log('Loaded week data:', weekData)
      } catch (error) {
        console.error('Error fetching or creating the current week:', error)
      }
    },
    addRecipe(event, day) {
      console.log('In addRecipe')
      const recipeData = event.dataTransfer.getData('application/json')
      if (recipeData) {
        const recipe = JSON.parse(recipeData)
        this.weeklyList[day].push(recipe)
        console.log(
          'All recipes saved in ' + day + ': ' + JSON.stringify(this.weeklyList[day], null, 2),
        )
      }
    },
    deleteRecipe(day, index) {
      this.weeklyList[day].splice(index, 1)
    },
    // ClearWeeklyList nwe 11Dec
    clearWeeklyList() {
      Object.keys(this.weeklyList).forEach(day => {
        this.weeklyList[day] = [];
      });
    }
  },
  mounted() {
    this.fetchOrCreateWeek();
  },
}
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
