<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import router from "@/router/index.js";
import "chart.js/auto";
import { Chart } from "chart.js";
import { getUserId } from "@/storage/localStorageManagement.js";

const doughnutChartEl = ref(null);
const barChartEl = ref(null);
const lineChartEl = ref(null);
const recipeFrequencyChartEl = ref(null);

const statistics = ref(null);

const fetchStatistics = async () => {
  try {
    const userId = getUserId();
    const response = await axios.get(`http://localhost:8080/users/${userId}/statistics`);
    statistics.value = response.data;
  } catch (error) {
    console.error("Error fetching statistics:", error);
    alert("Failed to load statistics.");
  }
};

const navigateTo = (page) => {
  router.push({ name: page });
};

const initCharts = () => {
  if (!statistics.value) return;

  const foodTypeData = statistics.value.foodTypeCounts || {};
  const labelsFood = Object.keys(foodTypeData);
  const countsFood = Object.values(foodTypeData);

  new Chart(doughnutChartEl.value, {
    type: "doughnut",
    data: {
      labels: labelsFood,
      datasets: [
        {
          label: "Verteilung Essensarten",
          data: countsFood,
          backgroundColor: ["#f0ad4e", "#5bc0de", "#5cb85c", "#d9534f", "#337ab7"],
          borderColor: "#222",
          borderWidth: 2,
        },
      ],
    },
    options: {
      plugins: {
        legend: {
          labels: {
            color: "#eee",
          },
        },
      },
    },
  });

  const ingredientData = statistics.value.ingredientCounts || {};
  const labelsIng = Object.keys(ingredientData);
  const countsIng = Object.values(ingredientData);

  new Chart(barChartEl.value, {
    type: "bar",
    data: {
      labels: labelsIng,
      datasets: [
        {
          label: "Zutatenverbrauch",
          data: countsIng,
          backgroundColor: "#5bc0de",
          borderColor: "#222",
          borderWidth: 1,
        },
      ],
    },
    options: {
      scales: {
        x: {
          ticks: { color: "#eee" },
          grid: { color: "rgba(255,255,255,0.2)" },
        },
        y: {
          ticks: { color: "#eee" },
          grid: { color: "rgba(255,255,255,0.2)" },
        },
      },
      plugins: {
        legend: {
          labels: {
            color: "#eee",
          },
        },
      },
    },
  });

  const weeklyCookingTimes = statistics.value.weeklyCookingTimes || {};
  const labelsWeeks = Object.keys(weeklyCookingTimes);
  const dataWeeklyTimes = Object.values(weeklyCookingTimes);

  const avgTimeData = Array(labelsWeeks.length).fill(statistics.value.averageTimePerRecipe);

  new Chart(lineChartEl.value, {
    type: "line",
    data: {
      labels: labelsWeeks,
      datasets: [
        {
          label: "Kochzeit pro Woche (Minuten)",
          data: dataWeeklyTimes,
          borderColor: "#5bc0de",
          borderWidth: 2,
        },
        {
          label: "Durchschnittliche Kochzeit pro Rezept (Minuten)",
          data: avgTimeData,
          borderColor: "#f0ad4e",
          borderDash: [5, 5],
          borderWidth: 2,
        },
      ],
    },
    options: {
      scales: {
        x: {
          ticks: { color: "#eee" },
          grid: { color: "rgba(255,255,255,0.2)" },
        },
        y: {
          ticks: { color: "#eee" },
          grid: { color: "rgba(255,255,255,0.2)" },
        },
      },
      plugins: {
        legend: {
          labels: {
            color: "#eee",
          },
        },
      },
    },
  });

  const recipeFrequency = statistics.value.recipeFrequency || {};
  const labelsRecipes = Object.keys(recipeFrequency);
  const countsRecipes = Object.values(recipeFrequency);

  new Chart(recipeFrequencyChartEl.value, {
    type: "bar",
    data: {
      labels: labelsRecipes,
      datasets: [
        {
          label: "Rezept-Häufigkeit",
          data: countsRecipes,
          backgroundColor: "#f0ad4e",
          borderColor: "#222",
          borderWidth: 1,
        },
      ],
    },
    options: {
      scales: {
        x: {
          ticks: { color: "#eee" },
          grid: { color: "rgba(255,255,255,0.2)" },
        },
        y: {
          ticks: { color: "#eee" },
          grid: { color: "rgba(255,255,255,0.2)" },
        },
      },
      plugins: {
        legend: {
          labels: {
            color: "#eee",
          },
        },
      },
    },
  });
};

onMounted(async () => {
  await fetchStatistics();
  initCharts();
});
</script>

<template>
  <div class="statistics-container">
    <div class="statistics-content">
      <h1>Statistiken</h1>

      <div v-if="statistics" class="statistics-grid">
        <div class="statistics-overview">
          <h3>Überblick</h3>
          <p>Gesamtzeit beim Kochen: <strong>{{ statistics.totalTime }} Minuten</strong></p>
          <p>Anzahl gekochter Rezepte: <strong>{{ statistics.recipeCount }}</strong></p>
          <p>Durchschnittliche Kochzeit pro Rezept: <strong>{{ statistics.averageTimePerRecipe }} Minuten</strong></p>
        </div>

        <div class="chart-container">
          <div class="chart-item">
            <h4>Kochzeit pro Woche</h4>
            <canvas ref="lineChartEl"></canvas>
          </div>

          <div class="chart-item">
            <h4>Essensarten</h4>
            <canvas ref="doughnutChartEl"></canvas>
          </div>

          <div class="chart-item">
            <h4>Zutatenverbrauch</h4>
            <canvas ref="barChartEl"></canvas>
          </div>

          <div class="chart-item">
            <h4>Rezept-Häufigkeit</h4>
            <canvas ref="recipeFrequencyChartEl"></canvas>
          </div>
        </div>
      </div>

      <div v-else>
        <p>Lade Statistiken...</p>
      </div>
    </div>

    <div class="dashboard-navigation">
      <ul class="nav">
        <li @click="navigateTo('history')">Historie</li>
        <li @click="navigateTo('shoppingList')">Einkaufszettel</li>
        <li @click="navigateTo('home')">Planner</li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
.statistics-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #1f1f1f;
  color: white;
}

.statistics-content {
  flex: 1;
  padding: 2rem;
  overflow-y: auto;
}

.statistics-grid {
  display: grid;
  grid-template-columns: 1fr 3fr;
  gap: 2rem;
}

.statistics-overview {
  background-color: #2b2b2b;
  border-radius: 8px;
  padding: 1rem;
}

.chart-container {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.chart-item {
  flex: 1 1 calc(50% - 1rem);
  background-color: #2b2b2b;
  border-radius: 8px;
  padding: 1rem;
  text-align: center;
}

.chart-item canvas {
  max-width: 100%;
  height: 250px;
}

.dashboard-navigation {
  flex: 0 0 10%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #1f1f1f;
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

.nav li {
  list-style-type: none;
  padding: 1rem;
  background-color: #2b2b2b;
  border-radius: 8px;
  color: #fff;
  transition: background-color 0.3s ease, color 0.3s ease;
  border: 2px solid #333;
}

.nav li:hover {
  background-color: white;
  color: #1f1f1f;
  cursor: pointer;
}
</style>
