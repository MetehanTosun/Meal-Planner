<script setup>
import { useWeekStore } from '@/state-management/index.js'
import { onMounted, ref } from 'vue'
import ReactiveDashboardShoppingList from '@/components/components/Dashboards/ReactiveDashboardShoppingList.vue'

const weekStore = useWeekStore();
const isLoading = ref(true)

onMounted(async () => {
  try {
    await weekStore.fetchWeeksInRange(2)
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <div class="shopping-list-container">
    <h1>Einkaufsliste für die aktuelle Woche</h1>
    <table class="shopping-list-table">
      <thead>
      <tr>
        <th>Zutat</th>
        <th>Menge</th>
        <th>Einheit</th>
      </tr>
      </thead>
      <tbody>
      <tr
        v-for="(item, index) in weekStore.getShoppingList"
        :key="item.name + '_' + item.unit + '_' + index"
      >
        <td>{{ item.name }}</td>
        <td>{{ item.amount }}</td>
        <td>{{ item.unit }}</td>
      </tr>
      </tbody>
    </table>
  </div>
  <ReactiveDashboardShoppingList/>
</template>

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
</style>