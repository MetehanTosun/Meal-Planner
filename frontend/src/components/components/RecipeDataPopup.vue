<script setup>
import { defineProps, defineEmits } from 'vue';

defineProps({
  recipe: {
    type: Object,
    required: true,
  },
  show: {
    type: Boolean,
    required: true,
  },
});

const emit = defineEmits(['close']);

const closePopup = () => {
  emit('close');
};
</script>

<template>
  <div v-if="show" class="popup-overlay" @click.self="closePopup">
    <div class="popup">
      <h2 class="popup-title">Rezept: {{ recipe.recipeData.name }}</h2>
      <p class="popup-detail">
        <strong>Zeitaufwand:</strong> {{ recipe.recipeData.time }} Minuten
      </p>
      <p class="popup-detail">
        <strong>Typ:</strong>
        <span v-if="recipe.recipeData.foodType=== VEGETARIAN"> Vegetarisch</span>
        <span v-else-if="recipe.recipeData.foodType=== VEGAN"> Vegan</span>
        <span v-else> Enthält Fleisch</span>

      </p>
      <h4 class="popup-subtitle">Zutaten:</h4>
      <ul class="popup-ingredients">
        <li
          v-for="(ingredient, index) in recipe.recipeData.ingredients"
          :key="index"
        >
          <b>{{ ingredient.name }}</b> :
          <span v-if="ingredient.unit === 'G'">{{ ingredient.amount }} Gramm</span>
          <span v-else-if="ingredient.unit === 'ML'">{{ ingredient.amount }} Milliliters</span>
          <span v-else-if="ingredient.unit === 'STÜCK'">{{ ingredient.amount }} Stück</span>
          <span v-else>{{ ingredient.amount }} {{ ingredient.unit }}</span>
        </li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.popup {
  background-color: #fff;
  color: #333;
  border-radius: 12px;
  padding: 20px 30px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
  max-width: 400px;
  width: 90%;
  text-align: left;
}

.popup-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 15px;
  color: #222;
}

.popup-subtitle {
  margin-top: 20px;
  font-size: 1.2rem;
  color: #444;
  font-weight: bold;
}

.popup-detail {
  margin: 5px 0;
  font-size: 1rem;
  color: #555;
}

.popup-ingredients {
  list-style: none;
  padding: 0;
  margin: 0;
}

.popup-ingredients li {
  margin: 5px 0;
  font-size: 0.95rem;
  color: #555;
}
</style>
