<template>
  <div v-if="showModal" class="modal-overlay">
    <div class="modal-content">
      <h2>Neues Rezept erstellen</h2>

      <!-- Recipename -->
      <div class="form-group">
        <label>Rezeptname:</label>
        <input v-model="recipe.name" type="text" placeholder="Rezeptname eingeben">
      </div>

      <!-- Prepare time -->
      <div class="form-group">
        <label>Zubereitungszeit (Minuten):</label>
        <input v-model="recipe.time" type="number" min="1">
      </div>

      <!-- Ingredients -->
      <div class="ingredients-section">
        <h3>Zutaten</h3>
        <div v-for="(ingredient, index) in recipe.ingredients" :key="index" class="ingredient-row">
          <input v-model="ingredient.name" type="text" placeholder="Zutat">
          <input v-model="ingredient.amount" type="number" min="0" placeholder="Menge">
          <select v-model="ingredient.unit">
            <option value="G">Gramm</option>
            <option value="ML">Milliliter</option>
            <option value="STÜCK">Stück</option>
          </select>
          <button @click="removeIngredient(index)" class="remove-btn">-</button>
        </div>
        <button @click="addIngredient" class="add-btn">+ Zutat hinzufügen</button>
      </div>

      <!-- Foodtype -->
      <div class="form-group">
        <label>Art des Gerichts:</label>
        <select v-model="recipe.foodType">
          <option value="VEGAN">Vegan</option>
          <option value="VEGETARIAN">Vegetarisch</option>
          <option value="MEAT">Fleisch</option>
        </select>
      </div>

      <!-- Buttons -->
      <div class="modal-actions">
        <button @click="saveRecipe" class="save-btn">Speichern</button>
        <button @click="closeModal" class="cancel-btn">Abbrechen</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const showModal = ref(false)
const recipe = reactive({
  name: '',
  time: null,
  ingredients: [
    {
      name: '',
      amount: null,
      unit: 'G'
    }
  ],
  foodType: 'MEAT',
  instructions: []
})

const addIngredient = () => {
  recipe.ingredients.push({
    name: '',
    amount: null,
    unit: 'G'
  })
}

const removeIngredient = (index) => {
  recipe.ingredients.splice(index, 1)
}

const saveRecipe = () => {
  // Hier API-Aufruf implementieren
  console.log('Recipe to save:', recipe)
  closeModal()
}

const closeModal = () => {
  showModal.value = false
  // Reset form
  recipe.name = ''
  recipe.time = null
  recipe.ingredients = [{
    name: '',
    amount: null,
    unit: 'G'
  }]
  recipe.foodType = 'MEAT'
}

// Expose necessary methods
defineExpose({
  showModal,
  closeModal
})
</script>

<style scoped>
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
  max-width: 600px;
  max-height: 80vh;
  overflow-y: auto;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
}

input, select {
  width: 100%;
  padding: 0.5rem;
  margin-bottom: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.ingredients-section {
  margin: 1rem 0;
}

.ingredient-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr auto;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.add-btn, .remove-btn {
  padding: 0.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.add-btn {
  background-color: #4CAF50;
  color: white;
  width: 100%;
  margin-top: 1rem;
}

.remove-btn {
  background-color: #f44336;
  color: white;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}

.save-btn, .cancel-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.save-btn {
  background-color: #4CAF50;
  color: white;
}

.cancel-btn {
  background-color: #f44336;
  color: white;
}
</style>
