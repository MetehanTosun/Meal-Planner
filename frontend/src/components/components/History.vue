<script setup>
import { ref } from "vue";
import router from "@/router/index.js";

const changeTo = (value) => {
  if (value === "statistics") {
    router.push({ name: "statistics" });
  } else if (value === "shoppingList") {
    router.push({ name: "shoppingList" });
  } else if (value === "dashboard") {
    router.push({ name:"dashboard"})
  }
};
const weeks = ref([
  {
    id: 1,
    startDate: "2023-01-01",
    endDate: "2023-01-07",
    days: [
      {
        id: 101,
        date: "2023-01-01",
        userSpecificRecipes: [
          { id: 1, name: "Spaghetti Carbonara", portions: 2 },
          { id: 2, name: "Caesar Salad", portions: 1 },
          { id: 3, name: "Pancakes", portions: 3 },
          { id: 3, name: "Kuchen", portions: 4 },
        ],
      },
      {
        id: 102,
        date: "2023-01-02",
        userSpecificRecipes: [
          { id: 3, name: "Pancakes", portions: 3 },
        ],
      },
      {
        id: 102,
        date: "2023-01-03",
        userSpecificRecipes: [
          { id: 3, name: "Pancakes", portions: 3 },
        ],
      },
      {
        id: 102,
        date: "2023-01-04",
        userSpecificRecipes: [
          { id: 3, name: "Pancakes", portions: 3 },
        ],
      },
      {
        id: 102,
        date: "2023-01-05",
        userSpecificRecipes: [
          { id: 3, name: "Pancakes", portions: 3 },
        ],
      },
      {
        id: 102,
        date: "2023-01-06",
        userSpecificRecipes: [
          { id: 3, name: "Pancakes", portions: 3 },
        ],
      },
      {
        id: 102,
        date: "2023-01-07",
        userSpecificRecipes: [
          { id: 3, name: "Pancakes", portions: 3 },
        ],
      },
    ],
  },
  {
    id: 2,
    startDate: "2023-01-08",
    endDate: "2023-01-14",
    days: [
      {
        id: 103,
        date: "2023-01-08",
        userSpecificRecipes: [
          { id: 4, name: "Pizza Margherita", portions: 1 },
        ],
      },

    ],
  },
  {
    id: 3,
    startDate: "2023-01-15",
    endDate: "2023-01-22",
    days: [
      {
        id: 103,
        date: "2023-01-08",
        userSpecificRecipes: [
          { id: 4, name: "Pizza Margherita", portions: 1 },
        ],
      },

    ],
  },
  {
    id: 4,
    startDate: "2023-01-15",
    endDate: "2023-01-22",
    days: [
      {
        id: 103,
        date: "2023-01-08",
        userSpecificRecipes: [
          { id: 4, name: "Pizza Margherita Fdsgkansgojnasgjidnasjdognasiudgbasuigdb", portions: 1 },
        ],
      },

    ],
  },
  {
    id: 5,
    startDate: "2023-01-15",
    endDate: "2023-01-22",
    days: [
      {
        id: 103,
        date: "2023-01-08",
        userSpecificRecipes: [
          { id: 4, name: "Pizza Margherita", portions: 1 },
        ],
      },

    ],
  },
]);

const formatDate = (date) => {
  const options = { year: "numeric", month: "long", day: "numeric" };
  return new Date(date).toLocaleDateString("de-DE", options);
};
</script>


<template>
  <div class="weeks-dashboard-container">
    <!-- Scrollbarer Bereich für Wochen -->
    <div class="weeks-container">
      <h1>Wochenübersicht</h1>

      <div class="week" v-for="week in weeks" :key="week.id">
        <h2>Woche vom {{ formatDate(week.startDate) }} bis {{ formatDate(week.endDate) }}</h2>

        <!-- Tage in der Woche -->
        <div class="days-container">
          <div class="day" v-for="day in week.days" :key="day.id">
            <h3>{{ formatDate(day.date) }}</h3>
            <ul class="recipes">
              <li v-for="recipe in day.userSpecificRecipes" :key="recipe.id">
                {{ recipe.name }} | Portionen: {{ recipe.portions }}
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
  width: 100%;
  word-wrap: break-word;
  word-break: break-word;
  box-sizing: border-box;
}

.day h3 {
  color: #f0ad4e;
  padding: 0.5rem;
  margin: 0 0 1rem 0;
  text-align: center;
  background-color: transparent;
  width: 100%;
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




