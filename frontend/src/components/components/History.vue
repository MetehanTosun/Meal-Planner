<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import router from "@/router/index.js";
import { getUserId } from "@/storage/localStorageManagement.js"

const changeTo = (value) => {
  if (value === "statistics") {
    router.push({ name: "statistics" });
  } else if (value === "shoppingList") {
    router.push({ name: "shoppingList" });
  } else if (value === "dashboard") {
    router.push({ name: "home" });
  }
};

const weeks = ref([]);

const fetchWeeks = async () => {
  try {
    const userId = getUserId();
    const response = await axios.get(`http://localhost:8080/weeks/user/${userId}`);
    weeks.value = response.data;
  } catch (error) {
    console.error("Fehler beim Laden der Wochen:", error);
  }
};

const formatDate = (date) => {
  const options = { year: "numeric", month: "long", day: "numeric" };
  return new Date(date).toLocaleDateString("de-DE", options);
};

onMounted(fetchWeeks);
</script>

<template>
  <div class="weeks-dashboard-container">

    <div class="weeks-container">
      <h1>Wochenübersicht</h1>

      <div class="week" v-for="week in weeks" :key="week.id">
        <h2>Woche vom {{ formatDate(week.startDate) }} bis {{ formatDate(week.endDate) }}</h2>


        <div class="days-container">
          <div class="day" v-for="day in week.days" :key="day.id">
            <h3>{{ formatDate(day.date) }}</h3>
            <ul class="recipes">
              <li v-for="recipe in day.userSpecificRecipes" :key="recipe.id">
                <strong>{{ recipe.recipeData.name }}</strong> | Portionen: {{ recipe.portions }}
              </li>

              <li v-if="day.userSpecificRecipes.length === 0" class="no-recipes">
                Keine Rezepte für diesen Tag
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <!-- Dashboard -->
    <div class="dashboard">
      <ul class="nav">
        <li class="nav-item" @click="changeTo('statistics')">Statistiken</li>
        <li class="nav-item" @click="changeTo('shoppingList')">Einkaufszettel</li>
        <li class="nav-item" @click="changeTo('dashboard')">Planner</li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
.weeks-dashboard-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

.weeks-container {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
  background-color: #1f1f1f;
  color: white;
}

.week {
  margin-bottom: 2rem;
  border: 1px solid #333;
  border-radius: 8px;
  padding: 1rem;
  background-color: #2b2b2b;
}

.days-container {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.day {
  flex: 1 1 calc(14% - 1rem);
  max-width: calc(14% - 1rem);
  background-color: #1f1f1f;
  padding: 1rem;
  border: 1px solid #444;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: stretch;
  box-sizing: border-box;
}

.recipes {
  list-style-type: none;
  width: 100%;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.recipes li {
  background-color: #333;
  padding: 0.5rem;
  border-radius: 4px;
  text-align: center;
  word-wrap: break-word;
  word-break: break-word;
}

.no-recipes {
  background-color: transparent;
  color: #ccc;
  text-align: center;
  font-style: italic;
}

.day h3 {
  color: #f0ad4e;
  padding: 0.5rem;
  margin: 0 0 1rem 0;
  text-align: center;
}

.dashboard {
  height: 10%;
  background-color: #1f1f1f;
  display: flex;
  justify-content: center;
  align-items: center;
  border-top: 1px solid #333;
}

.nav {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 0.5rem;
}

.nav-item {
  list-style-type: none;
  padding: 1rem;
  background-color: #2b2b2b;
  border-radius: 8px;
  color: #fff;
  transition: background-color 0.3s ease, color 0.3s ease;
  border: 2px solid #333;
}

.nav-item:hover {
  background-color: white;
  color: #1f1f1f;
  cursor: pointer;
}
</style>
