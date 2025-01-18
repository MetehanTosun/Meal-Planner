<template>
    <div v-if="showModal" class="modal-overlay">
      <div class="modal-content">
        <h2>Rezept löschen</h2>
        <p>Sind Sie sicher, dass Sie dieses Rezept löschen möchten?</p>
        <div class="modal-buttons">
          <button class="cancel-button" @click="closeModal">Abbrechen</button>
          <button class="confirm-button" @click="confirmDelete">Löschen</button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  
  const showModal = ref(false);
  const recipeToDelete = ref(null);
  const emit = defineEmits(['confirm']);
  
  const openModal = (recipe) => {
    recipeToDelete.value = recipe;
    showModal.value = true;
  };
  
  const closeModal = () => {
    showModal.value = false;
    recipeToDelete.value = null;
  };
  
  const confirmDelete = () => {
    emit('confirm', recipeToDelete.value);
    closeModal();
  };
  
  defineExpose({
    openModal,
    closeModal
  });
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
    background-color: #1f1f1f;
    padding: 20px;
    border-radius: 8px;
    max-width: 400px;
    width: 90%;
    border: 1px solid #fff;
    color: white;
  }
  
  h2 {
    margin-top: 0;
    margin-bottom: 16px;
    font-size: 1.5rem;
  }
  
  p {
    margin-bottom: 20px;
  }
  
  .modal-buttons {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
  
  .cancel-button {
    padding: 8px 16px;
    border: none;
    border-radius: 4px;
    background-color: #6c757d;
    color: white;
    cursor: pointer;
    transition: background-color 0.2s;
  }
  
  .cancel-button:hover {
    background-color: #5a6268;
  }
  
  .confirm-button {
    padding: 8px 16px;
    border: none;
    border-radius: 4px;
    background-color: #dc3545;
    color: white;
    cursor: pointer;
    transition: background-color 0.2s;
  }
  
  .confirm-button:hover {
    background-color: #c82333;
  }
  </style>