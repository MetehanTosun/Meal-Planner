<template>
  <!-- Modal overlay - only shown when showModal is true -->
  <div v-if="showModal" class="modal-overlay">
    <div class="modal-content">
      <h2>Rezept teilen</h2>

      <!-- Username input form group -->
      <div class="form-group">
        <label>Benutzername:</label>
        <input
          v-model="username"
          type="text"
          placeholder="Benutzername eingeben"
          @keyup.enter="shareRecipe"
        >
      </div>

      <!-- Action buttons -->
      <div class="modal-actions">
        <button @click="shareRecipe" class="save-btn">Teilen</button>
        <button @click="closeModal" class="cancel-btn">Abbrechen</button>
      </div>
    </div>
  </div>
</template>

<script setup>
// Import required Vue composition API and axios
import { ref } from 'vue'
import axios from '@/axios'

// Reactive state variables
const showModal = ref(false)    // Controls modal visibility
const username = ref('')        // Stores the entered username
const recipeId = ref(null)      // Stores the ID of the recipe to be shared

/**
 * Handles the recipe sharing process
 * Validates input, sends API request, and handles response
 */
const shareRecipe = async () => {
  try {
    // Validate username input
    if (!username.value.trim()) {
      alert('Bitte geben Sie einen Benutzernamen ein')
      return
    }

    // Prepare data for API request
    const shareRecipeDTO = {
      recipeId: recipeId.value,
      receiverName: username.value.trim()
    }

    // Send PUT request to share the recipe
    const response = await axios.put('/recipes/share', shareRecipeDTO)
    console.log('Share response:', response)
    alert('Rezept erfolgreich geteilt!')
    closeModal()
  } catch (error) {
    // Handle any errors that occur during the sharing process
    console.error('Error sharing recipe:', error)
    const errorMsg = error.response?.data || 'Ein Fehler ist aufgetreten'
    alert(errorMsg)
  }
}

/**
 * Opens the modal and sets the recipe ID
 * @param {number} id - The ID of the recipe to be shared
 */
const openModal = (id) => {
  recipeId.value = id
  username.value = ''  // Clear any previous username
  showModal.value = true
}

/**
 * Closes the modal and resets all form data
 */
const closeModal = () => {
  showModal.value = false
  username.value = ''
  recipeId.value = null
}

defineExpose({
  showModal,
  openModal,
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
    background: #1f1f1f;
    color: white;
    padding: 2rem;
    border-radius: 8px;
    width: 90%;
    max-width: 400px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }

  h2 {
    margin-top: 0;
    margin-bottom: 1.5rem;
    font-size: 1.5rem;
    text-align: center;
  }

  .form-group {
    margin-bottom: 1.5rem;
  }

  .form-group label {
    display: block;
    margin-bottom: 0.5rem;
    font-weight: 500;
  }

  input {
    width: 100%;
    padding: 0.5rem;
    margin-bottom: 0.5rem;
    border: 1px solid #fff;
    border-radius: 4px;
    background-color: #181818;
    color: white;
    font-size: 1rem;
  }

  input:focus {
    outline: none;
    border-color: #2196F3;
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
    font-size: 1rem;
    transition: background-color 0.2s ease;
  }

  .save-btn {
    background-color: #4CAF50;
    color: white;
  }

  .save-btn:hover {
    background-color: #45a049;
  }

  .cancel-btn {
    background-color: #f44336;
    color: white;
  }

  .cancel-btn:hover {
    background-color: #d32f2f;
  }
  </style>
