<template>
  <!-- Main modal container -->
  <div v-if="showModal" class="modal-overlay">
    <div class="modal-content">
      <h2>Neues Rezept erstellen</h2>
 
      <!-- Recipe name input field -->
      <div class="form-group">
        <label>Rezeptname: *</label>
        <input 
          v-model="recipe.name" 
          type="text" 
          placeholder="Rezeptname eingeben"
          :class="{ 'error': validationErrors.name }"
        >
      </div>
 
      <!-- Preparation time input -->
      <div class="form-group">
        <label>Zubereitungszeit (Minuten): *</label>
        <input 
          v-model="recipe.time" 
          type="number" 
          min="1"
          :class="{ 'error': validationErrors.time }"
        >
      </div>
 
      <!-- Dynamic ingredient rows -->
      <div v-for="(ingredient, index) in recipe.ingredients" :key="index" class="ingredient-row">
        <!-- Ingredient name -->
        <input 
          v-model="ingredient.name" 
          type="text" 
          placeholder="Zutat *"
          :class="{ 'error': validationErrors.ingredients?.[index]?.name }">
        <!-- Amount -->  
        <input 
          v-model="ingredient.amount" 
          type="number" 
          min="0" 
          placeholder="Menge *"
          :class="{ 'error': validationErrors.ingredients?.[index]?.amount }">
        <!-- Unit selection -->
        <select 
          v-model="ingredient.unit"
          :class="{ 'error': validationErrors.ingredients?.[index]?.unit }">
          <option value="G">Gramm</option>
          <option value="ML">Milliliter</option>
          <option value="STÜCK">Stück</option>
        </select>
        <!-- Ingredient type -->
        <select v-model="ingredient.ingredientType">
          <option v-for="(type, key) in INGREDIENT_TYPES" 
                  :key="key"
                  :value="key">
            {{ type.label }}
          </option>
        </select>
        <!-- Remove button -->
        <button @click="removeIngredient(index)" class="remove-btn">-</button>
      </div>
      <!-- Add new ingredient button -->
      <button @click="addIngredient" class="add-btn">+ Zutat hinzufügen</button>
 
      <!-- Instructions section -->
      <div class="instructions-section">
        <h3>Zubereitungsschritte</h3>
        <div v-for="(instruction, index) in recipe.instructions" :key="index" class="instruction-row">
          <div class="instruction-number">{{index + 1}}.</div>
          <input v-model="recipe.instructions[index]" type="text" placeholder="Zubereitungsschritt beschreiben">
          <button @click="removeInstruction(index)" class="remove-btn">-</button>
        </div>
        <button @click="addInstruction" class="add-btn">+ Schritt hinzufügen</button>
      </div>
 
      <!-- Food type selection -->
      <div class="form-group">
        <label>Art des Gerichts: *</label>
        <select 
          v-model="recipe.foodType"
          :class="{ 'error': validationErrors.foodType }"
        >
          <option value="VEGAN">Vegan</option>
          <option value="VEGETARIAN">Vegetarisch</option>
          <option value="MEAT">Fleisch</option>
        </select>
      </div>
 
      <!-- Error message display -->
      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>
 
      <!-- Action buttons -->
      <div class="modal-actions">
        <button @click="saveRecipe" class="save-btn">Speichern</button>
        <button @click="closeModal" class="cancel-btn">Abbrechen</button>
      </div>
    </div>
  </div>
 </template>
 
 <script setup>
 // Import required dependencies
 import { ref, reactive } from 'vue'
 import axios from '@/axios'
 import { INGREDIENT_TYPES } from '@/classes/IngredientTypes'
 
 // State management
 const showModal = ref(false)
 const errorMessage = ref('')
 const validationErrors = reactive({})
 
 // Initial recipe data structure
 const recipe = reactive({
  name: '',
  time: null,
  ingredients: [
    {
      name: '',
      amount: null,
      unit: 'G',
      foodType: 'MEAT',
      ingredientType: 'NONE'
    }
  ],
  instructions: [''],
  foodType: 'MEAT'
 })
 
 // Add new ingredient
 const addIngredient = () => {
  recipe.ingredients.push({
    name: '',
    amount: null,
    unit: 'G',
    foodType: 'MEAT',
    ingredientType: 'NONE'
  })
 }
 
 // Remove ingredient at index
 const removeIngredient = (index) => {
  recipe.ingredients.splice(index, 1)
 }
 
 // Add new instruction step
 const addInstruction = () => {
  recipe.instructions.push('')
 }
 
 // Remove instruction at index
 const removeInstruction = (index) => {
  recipe.instructions.splice(index, 1)
 }
 
 // Validate recipe data
 const validateRecipe = () => {
  validationErrors.value = {}
  const errors = {}
 
  // Check required fields
  if (!recipe.name?.trim()) {
    errors.name = true
  }
  if (!recipe.time || recipe.time <= 0) {
    errors.time = true
  }
  if (!recipe.foodType) {
    errors.foodType = true
  }
 
  // Check ingredients if present
  if (recipe.ingredients.length > 0) {
    errors.ingredients = recipe.ingredients.map(ingredient => {
      const ingredientErrors = {}
      if (!ingredient.name?.trim()) ingredientErrors.name = true
      if (!ingredient.amount || ingredient.amount < 0) ingredientErrors.amount = true
      if (!ingredient.unit) ingredientErrors.unit = true
      return Object.keys(ingredientErrors).length ? ingredientErrors : null
    })
    
    if (errors.ingredients.every(error => error === null)) {
      delete errors.ingredients
    }
  }
 
  Object.assign(validationErrors, errors)
  
  if (Object.keys(errors).length) {
    throw new Error('Bitte füllen Sie alle Pflichtfelder (*) aus')
  }
 }
 
 // Save recipe to backend
 const saveRecipe = async () => {
  try {
    validateRecipe()
 
    // Prepare data for API
    const recipeToSend = {
      name: recipe.name,
      time: parseInt(recipe.time),
      foodType: recipe.foodType,
      ingredients: recipe.ingredients
        .filter(ingredient => ingredient.name.trim())
        .map(ingredient => ({
          name: ingredient.name,
          amount: parseInt(ingredient.amount) || 0,
          unit: ingredient.unit,
          foodType: ingredient.foodType,
          ingredientType: ingredient.ingredientType
        })),
      instructions: recipe.instructions.filter(instruction => instruction.trim())
    }
 
    await axios.post('/recipes', recipeToSend)
    closeModal()
    emit('recipe-created')
  } catch (error) {
    console.error('Save recipe error:', error)
    errorMessage.value = error.response?.data?.message || error.message
  }
 }
 
 // Reset form and close modal
const closeModal = () => {
  showModal.value = false
  errorMessage.value = ''
  
  // Reset validation errors
  Object.keys(validationErrors).forEach(key => {
    delete validationErrors[key]
  })
  
  // Reset recipe data
  recipe.name = ''
  recipe.time = null
  recipe.ingredients = [{
    name: '',
    amount: null,
    unit: 'G',
    foodType: 'MEAT',
    ingredientType: 'NONE'
  }]
  recipe.instructions = ['']
  recipe.foodType = 'MEAT'
}
 
 // Event emitter setup
 const emit = defineEmits(['recipe-created'])
 
 // Expose required methods
 defineExpose({
  showModal,
  closeModal
 })
 </script>
 
 <style scoped>
 /* Modal overlay styling */
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
 
 /* Content container */
 .modal-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow-y: auto;
 }
 
 /* Form group layout */
 .form-group {
  margin-bottom: 1rem;
 }
 
 .form-group label {
  display: block;
  margin-bottom: 0.5rem;
 }
 
 /* Form controls styling */
 input, select {
  width: 100%;
  padding: 0.5rem;
  margin-bottom: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
 }
 
 /* Error state */
 input.error, select.error {
  border-color: #f44336;
 }
 
 /* Error message display */
 .error-message {
  color: #f44336;
  margin-top: 1rem;
  padding: 0.5rem;
  background-color: #ffebee;
  border-radius: 4px;
 }
 
 /* Ingredient row grid layout */
 .ingredient-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr auto;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
 }
 
 /* Instruction row layout */
 .instruction-row {
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
  align-items: center;
 }
 
 .instruction-number {
  padding: 0.5rem;
  font-weight: bold;
 }
 
 /* Button styling */
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
 
 /* Action buttons container */
 .modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
 }
 
 /* Action button styles */
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