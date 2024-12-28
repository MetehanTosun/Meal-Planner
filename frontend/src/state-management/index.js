import { defineStore } from 'pinia';
import axios from '@/axios';
import { getUserId } from '@/storage/localStorageManagement.js'

export const useWeekStore = defineStore('week', {
  state: () => ({
    currentWeek: null, // The current week's data
    days: [],          // The days associated with the current week
  }),
  actions: {
    /**
     * Set the current week data and update the days array.
     * @param {Object} week - Week data object from the backend.
     */
    setWeekData(week) {
      if (!week) {
        console.error('setWeekData: Invalid week data:', week);
        return;
      }

      this.currentWeek = week;
      this.days = week.days || []; // Ensure days is always an array
      console.log('Week data updated:', this.currentWeek);
    },
    async fetchCurrentWeek() {
      try {
        console.log('Fetching current week...');
        console.log('userId ', getUserId());
        const response = await axios.get(`/weeks/current/${getUserId()}`);
        this.currentWeek = response.data;
        this.days = response.data.days || []; // Ensure days are always an array
        console.log('Current week fetched successfully:', this.currentWeek);
      } catch (error) {
        console.error('Error fetching current week:', error);
        throw error;
      }
    },
    async createDefaultWeek(userId) {
      try {
        const response = await axios.post(`/weeks/create/user/${userId}`, {
          startDate: new Date().toISOString(),
        });
        console.log('Default week created successfully:', response.data);
        this.currentWeek = response.data;
        this.days = response.data.days || []; // Update the store state immediately
        return response.data;
      } catch (error) {
        console.error('Error creating default week:', error);
        throw error;
      }
    },
    // Add a recipe to a day and re-fetch the week to ensure consistency
    async addRecipeToDay(dayId, recipeId, portions) {
      try {
        console.log('Adding recipe to day:', dayId);
        console.log('Recipe ID:', recipeId);
        console.log('Portions:', portions);
        await axios.post('/days/add-recipe', {
          dayId: dayId,
          recipeId: recipeId,
          portions: portions,
        });
        console.log(
          `Recipe with ID ${recipeId} added to day with ID ${dayId}`
        );

        // Re-fetch the current week after successfully adding a recipe
        console.log('Fetching the currentWeek.')
        if (this.currentWeek) {
          await this.fetchCurrentWeek();
        }
      } catch (error) {
        console.error(
          `Error adding recipe with ID ${recipeId} to day with ID ${dayId}:`,
          error
        );
        throw error;
      }
    },

    // Remove a recipe from a day and re-fetch the week to ensure consistency
    async removeRecipeFromDay(dayId, recipeId) {
      try {
        await axios.delete(`/days/${dayId}/recipes/${recipeId}`);
        console.log(`Recipe with ID ${recipeId} removed from day with ID ${dayId}`);

        if (this.currentWeek) {
          await this.fetchCurrentWeek(this.currentWeek.userId);
        }
      } catch (error) {
        console.error(
          `Error removing recipe with ID ${recipeId} from day with ID ${dayId}:`,
          error
        );
        throw error;
      }
    },
    async incrementRecipePortions(recipeId) {
      try {
        console.log(`Incrementing portions for recipe ID ${recipeId}`);
        await axios.post(`/user-specific-recipes/${recipeId}/increment-portions`);
        console.log(`Portions incremented for recipe ID ${recipeId}`);
        if (this.currentWeek) {
          await this.fetchCurrentWeek(); // Refresh the week data to ensure consistency
        }
      } catch (error) {
        console.error(`Error incrementing portions for recipe ID ${recipeId}:`, error);
        throw error;
      }
    },
    async decrementRecipePortions(recipeId) {
      try {
        console.log(`Decrementing portions for recipe ID ${recipeId}`);
        await axios.post(`/user-specific-recipes/${recipeId}/decrement-portions`);
        console.log(`Portions decremented for recipe ID ${recipeId}`);
        if (this.currentWeek) {
          await this.fetchCurrentWeek(); // Refresh the week data to ensure consistency
        }
      } catch (error) {
        console.error(`Error decrementing portions for recipe ID ${recipeId}:`, error);
        throw error;
      }
    },
    // Clear the store state
    clearWeekData() {
      this.currentWeek = null;
      this.days = [];
    },
  },
  getters: {
    // Get a specific day by its ID
    getDayById: (state) => (dayId) => {
      return state.days.find((d) => d.id === dayId);
    },

    // Get all recipes for a specific day by its ID
    getRecipesForDay: (state) => (dayId) => {
      const day = state.days.find((d) => d.id === dayId);
      return day ? day.userSpecificRecipes || [] : [];
    },
  },
});
