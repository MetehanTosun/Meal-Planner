<<<<<<< HEAD
<template>
    <div>
      <!-- Button für Einkaufsliste -->
      <button 
        @click="openModal" 
        class="shopping-list-btn">
        Einkaufsliste generieren
      </button>
  
      <!-- Modales Fenster -->
      <div v-if="isModalOpen" class="modal-overlay">
        <div class="modal-content">
          <h2>Einkaufsliste</h2>
  
          <!-- Liste oder Nachricht anzeigen -->
          <div v-if="shoppingList.length > 0">
            <ul>
              <li v-for="(amount, ingredient) in shoppingList" :key="ingredient">
                {{ ingredient }}: {{ amount }}
              </li>
            </ul>
          </div>
          <div v-else>
            <p>Die Liste ist leer.</p>
          </div>
  
          <!-- Schließen-Button -->
          <button @click="closeModal" class="close-btn">Schließen</button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  
  const shoppingList = ref([]); // Initialisierung als leeres Array
  const recipes = ref([]);
  const isModalOpen = ref(false); // Zustand für das Modal
  
  // Funktion zum Öffnen des Modals
  const openModal = async () => {
    await generateShoppingList(); // Einkaufsliste generieren
    isModalOpen.value = true; // Modal öffnen
  };
  
  // Funktion zum Schließen des Modals
  const closeModal = () => {
    isModalOpen.value = false; // Modal schließen
  };
  
  // Funktion zum Generieren der Einkaufsliste
  const generateShoppingList = async () => {
    try {
      const response = await fetch("/shopping-list", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(recipes.value),
      });
  
      if (!response.ok) {
        throw new Error("Fehler beim Generieren der Einkaufsliste");
      }
  
      shoppingList.value = await response.json();
    } catch (error) {
      console.error("Fehler:", error);
    }
  };
  
  // Funktion zum Laden der Rezepte
  const loadRecipes = async () => {
    try {
      const response = await fetch("/recipes");
      if (!response.ok) {
        throw new Error("Fehler beim Laden der Rezepte");
      }
      recipes.value = await response.json();
    } catch (error) {
      console.error("Fehler:", error);
    }
  };
  
  // Rezepte laden
  loadRecipes();
  </script>
  
  <style scoped>
  /* Button Styling */
  .shopping-list-btn {
    position: fixed;
    bottom: 20px;
    left: 20px;
    padding: 10px 15px;
    background-color: #4CAF50;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    z-index: 10;
  }
  
  .shopping-list-btn:hover {
    background-color: #45a049;
  }
  
  /* Modal Styling */
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
  }
  
  .modal-content {
    background: white;
    padding: 2rem;
    border-radius: 8px;
    width: 90%;
    max-width: 400px;
    text-align: center;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }
  
  .close-btn {
    margin-top: 1rem;
    padding: 0.5rem 1rem;
    background-color: #f44336;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
  }
  
  .close-btn:hover {
    background-color: #e53935;
  }
  </style>
  
=======


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
  </div>
  <ReactiveDashboardShoppingList/>
</template>

<script setup>
  import { useWeekStore } from '@/state-management/index.js'
  import { onMounted, ref } from 'vue'
  import ReactiveDashboardShoppingList from '@/components/components/Dashboards/ReactiveDashboardShoppingList.vue'
  import { generateShoppingListPDF } from '@/utils/pdfGenerator'
  import { INGREDIENT_TYPES } from '@/classes/IngredientTypes'

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
      endDate
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
</style>
>>>>>>> user-specific-recipe
