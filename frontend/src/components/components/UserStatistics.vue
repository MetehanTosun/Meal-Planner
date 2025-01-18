<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import 'chart.js/auto'
import { Chart } from 'chart.js'
import { getUserId } from '@/storage/localStorageManagement.js'
import ReactiveDashboardStatistics from '@/components/components/Dashboards/ReactiveDashboardStatistics.vue'

const doughnutChartEl = ref(null)
const barChartEl = ref(null)

const statistics = ref(null)
const userId = getUserId()

const fetchStatistics = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/users/${userId}/statistics`)
    statistics.value = response.data
  } catch (error) {
    console.error(error)
    alert('Fehler beim Laden der Statistiken')
  }
}

const initCharts = () => {
  if (!statistics.value) return

  const foodTypeData = statistics.value.foodTypeCounts || {}
  const labelsFood = Object.keys(foodTypeData)
  const countsFood = Object.values(foodTypeData)

  countsFood.reduce((a, b) => a + b, 0)


  new Chart(doughnutChartEl.value, {
    type: 'doughnut',
    data: {
      labels: labelsFood,
      datasets: [
        {
          label: 'Verteilung Essensarten',
          data: countsFood,
          backgroundColor: [
            '#f0ad4e', // z.B. Orange
            '#5bc0de', // z.B. Hellblau
            '#5cb85c', // z.B. Grün
            '#d9534f', // z.B. Rot
            '#337ab7', // z.B. Blau
          ],
          borderColor: '#222',
          borderWidth: 2,
        },
      ],
    },
    options: {
      plugins: {
        legend: {
          labels: {
            color: '#eee',
          },
        },
      },
    },
  })

  const ingredientData = statistics.value.ingredientCounts || {}
  const labelsIng = Object.keys(ingredientData)
  const countsIng = Object.values(ingredientData)

  new Chart(barChartEl.value, {
    type: 'bar',
    data: {
      labels: labelsIng,
      datasets: [
        {
          label: 'Zutatenverbrauch',
          data: countsIng,
          backgroundColor: '#5bc0de',
          borderColor: '#222',
          borderWidth: 1,
        },
      ],
    },
    options: {
      scales: {
        x: {
          ticks: { color: '#eee' },
          grid: { color: 'rgba(255,255,255,0.2)' },
        },
        y: {
          ticks: { color: '#eee' },
          grid: { color: 'rgba(255,255,255,0.2)' },
        },
      },
      plugins: {
        legend: {
          labels: {
            color: '#eee',
          },
        },
      },
    },
  })
}

onMounted(async () => {
  await fetchStatistics()
  initCharts()
})
</script>

<template>
  <div class="stats-container">
    <div class="content">

      <div v-if="statistics" class="content-grid">
        <div class="stats-card">
          <h3>Überblick</h3>
          <p>Gesamtzeit beim Kochen: <strong>{{ statistics.totalTime }} Minuten</strong></p>
          <p>Anzahl gekochter Rezepte: <strong>{{ statistics.recipeCount }}</strong></p>
        </div>

        <div class="charts-container">
          <div class="chart-wrapper chart-doughnut">
            <h4>Essensarten</h4>
            <canvas ref="doughnutChartEl"></canvas>
          </div>

          <div class="chart-wrapper chart-bar">
            <h4>Zutatenverbrauch</h4>
            <canvas ref="barChartEl"></canvas>
          </div>
        </div>
      </div>

      <div v-else>
        <p>Lade Statistiken...</p>
      </div>
    </div>

    <div class="footer-stats">
      <ReactiveDashboardStatistics />
    </div>

  </div>
</template>
<style scoped>
.stats-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  margin: 0 auto;
  background-color: #1f1f1f;
  border-radius: 10px;
  color: #eee;
}

.content {
  flex: 1;
  overflow-y: auto;
  padding: 2rem;
}

.footer-stats {
  flex: 0 0 10%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #1f1f1f;
  border-top: 1px solid #333;
}

.header-section h2 {
  margin: 0;
  color: #fff;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 2rem;
}

.stats-card {
  background-color: #2b2b2b;
  border-radius: 8px;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
.stats-card h3 {
  margin: 0 0 0.5rem 0;
  color: #fff;
}
.stats-card p {
  margin: 0.25rem 0;
}

.charts-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.chart-wrapper {
  background-color: #2b2b2b;
  border-radius: 8px;
  padding: 1rem;
  text-align: center;
  overflow-x: auto;
}
.chart-wrapper h4 {
  margin-bottom: 0.5rem;
  color: #fff;
}
.chart-wrapper canvas {
  max-width: 100%;
  height: 300px;
}

@media (max-width: 768px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}

</style>
