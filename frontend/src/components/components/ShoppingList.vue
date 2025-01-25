<template>
  <div class="shopping-list-container">
    <div class="header-container">
      <h1>Einkaufsliste für die aktuelle Woche</h1>
    </div>
    <table class="shopping-list-table">
      <thead>
      <tr>
        <th>Zutat</th>
        <th>Menge</th>
        <th>Einheit</th>
        <th>Kategorie</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(item, index) in weekStore.getShoppingList"
          :key="item.name + '_' + item.unit + '_' + index">
        <td>{{ item.name }}</td>
        <td>{{ item.amount }}</td>
        <td>{{ item.unit }}</td>
        <td :style="{ backgroundColor: getIngredientTypeColor(item.ingredientType) }">
          {{ INGREDIENT_TYPES[item.ingredientType]?.label || 'Keine Kategorie' }}
        </td>
      </tr>
      </tbody>
    </table>
    <div class="button-container">
      <button
          @click="downloadPDF"
          class="download-button">
          PDF herunterladen
      </button>
    </div>
    <div class="dashboard-navigation">
      <ul class="nav">
        <li @click="navigateTo('history')">Historie</li>
        <li @click="navigateTo('statistics')">Statistik</li>
        <li @click="navigateTo('home')">Planner</li>
      </ul>
    </div>
  </div>

</template>

<script setup>
import { useWeekStore } from '@/state-management/index.js'
  import { onMounted, ref } from 'vue'
  import ReactiveDashboardShoppingList from '@/components/components/Dashboards/ReactiveDashboardShoppingList.vue'
  import { generateShoppingListPDF } from '@/utils/pdfGenerator'
  import { INGREDIENT_TYPES } from '@/classes/IngredientTypes'
  import router from "@/router/index.js";

const weekStore = useWeekStore()
const isLoading = ref(true)

const getIngredientTypeColor = (type) => {
  console.log('Type:', type, 'Color:', INGREDIENT_TYPES[type]?.color || INGREDIENT_TYPES.NONE.color);
  return INGREDIENT_TYPES[type]?.color || INGREDIENT_TYPES.NONE.color;
}

onMounted(async () => {
  try {
    await weekStore.fetchWeeksInRange(2)
  } finally {
    isLoading.value = false
  }
})

const navigateTo = (page) => {
  router.push({ name: page });
};

const downloadPDF = () => {
  const startDate = weekStore.getCurrentWeekStartDate;
  const endDate = weekStore.getCurrentWeekEndDate;

  if (!startDate || !endDate) {
    console.error('Start- oder Enddatum nicht verfügbar');
    return;
  }

  const pdf = generateShoppingListPDF(
    weekStore.getShoppingList,
    startDate,
    endDate,
    INGREDIENT_TYPES
  );

  pdf.download(`Einkaufszettel_${startDate.split('T')[0]}.pdf`);
};
</script>

<style scoped>
  .shopping-list-container {
    background-color: #1f1f1f;
    color: #fff;
    padding: 2rem;
    border-radius: 8px;
    max-width: 800px;
    margin: 1rem auto;
    overflow-y: auto;
  }

  .shopping-list-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 1rem;
  }

  .shopping-list-table th,
  .shopping-list-table td {
    border: 1px solid #fff;
    padding: 0.5rem;
    text-align: left;
  }

  .shopping-list-table th {
    background-color: #333;
  }

  .header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
  }

  .button-container {
    display: flex;
    justify-content: center;
    margin-top: 2rem;
  }

  .download-button {
    background-color: #333;
    color: white;
    border: none;
    padding: 0.5rem 1rem;
    border-radius: 8px;
    cursor: pointer;
    transition: background-color 0.2s;
  }

  .download-button:hover {
    background-color: #444;
  }
  .dashboard-navigation {
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 10%;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #1f1f1f;
    border-top: 1px solid #333;
    z-index: 100;
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
