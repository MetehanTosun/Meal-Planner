// src/state-management/weekStore.js

import { defineStore } from 'pinia';
import axios from '@/axios';
import { getUserId } from '@/storage/localStorageManagement.js';
//import router from '@/router'; // Ensure router is correctly imported

function isDayPast(dayDate) {
  const today = new Date();
  const dateToCheck = new Date(dayDate);

  // Normalize the dates to remove the time portion for accurate comparison
  today.setHours(0, 0, 0, 0);
  dateToCheck.setHours(0, 0, 0, 0);

  return dateToCheck < today;
}

export const useWeekStore = defineStore('week', {
  state: () => ({
    weeks: [], // All fetched weeks
    currentWeekIndex: 0, // Index of the currently displayed week
  }),
  actions: {
    /**
     * Fetch weeks in a range and set the current week.
     * @param {number} count - Number of weeks to fetch.
     */
    async fetchWeeksInRange(count = 2) {
      try {
        console.log('--- Fetching Weeks in Range ---');
        console.log(`Current Week Index Before Fetch: ${this.currentWeekIndex}`);
        console.log(`Requested Count: ${count}`);

        const response = await axios.get(`/weeks/range/${getUserId()}?count=${count}`);
        // Map weeks to include isPast flag for each day
        this.weeks = response.data
          .map(week => ({
            ...week,
            days: week.days.map(day => ({
              ...day,
              isPast: isDayPast(day.date),
            })),
          }));

        console.log('Fetched Weeks with isPast flags for days:', this.weeks);

        // Maintain currentWeekIndex within bounds
        if (this.weeks.length > this.currentWeekIndex) {
          // No change needed
        } else {
          this.currentWeekIndex = this.weeks.length - 1; // Fallback to last valid week
        }

        console.log(`Current Week Index After Fetch: ${this.currentWeekIndex}`);
        console.log('-------------------------------');
      } catch (error) {
        console.error('Error fetching weeks in range:', error);
        throw error;
      }
    },

    /**
     * Navigate to the previous week if available.
     */
    moveToPreviousWeek() {
      if (this.currentWeekIndex > 0) {
        this.currentWeekIndex -= 1;
      }
    },

    /**
     * Navigate to the next week if available.
     */
    moveToNextWeek() {
      if (this.currentWeekIndex < this.weeks.length - 1) {
        this.currentWeekIndex += 1;
      }
    },

    /**
     * Add a recipe to a day.
     * @param {number} dayId - The ID of the day to which the recipe will be added.
     * @param {number} recipeId - The ID of the recipe to add.
     * @param {number} portions - The number of portions for the recipe.
     */
    async addRecipeToDay(dayId, recipeId, portions) {
      try {
        const currentWeek = this.weeks[this.currentWeekIndex];
        const day = currentWeek.days.find(d => d.id === dayId);

        if (!day) {
          console.warn(`Day with ID ${dayId} not found in the current week.`);
          return;
        }

        if (day.isPast) {
          console.warn('Cannot add recipes to a past day.');
          return;
        }

        console.log(`Adding recipe ID ${recipeId} to day ID ${dayId} in week index ${this.currentWeekIndex}`);
        await axios.post('/days/add-recipe', {
          dayId: dayId,
          recipeId: recipeId,
          portions: portions,
        });
        console.log(`Recipe with ID ${recipeId} added to day with ID ${dayId}`);

        // Update local state without refetching
        const existingRecipe = day.userSpecificRecipes.find(r => r.recipeData.id === recipeId);


        const fetchRecipeData = async () => {
          try {
            const response = await axios.get(`/recipes/${recipeId}`);
            return response.data;
          } catch (error) {
            console.error(`Error fetching recipe data for ID ${recipeId}:`, error);
            return {
              id: recipeId,
              name: 'Unknown Recipe',
              foodType: 'MEAT',
              time: 0
            };
          }
        };

        const recipeData = await fetchRecipeData();
        console.log('Recipe data of to be added recipe:', recipeData);

        if (existingRecipe) {
          existingRecipe.portions += portions;
        } else {
          const newRecipe = {
            id: recipeId,
            recipeData: recipeData,
            portions,
          };
          day.userSpecificRecipes = [...day.userSpecificRecipes, newRecipe];
        }

        console.log(`Updated local state: week index ${this.currentWeekIndex} updated`);
      } catch (error) {
        console.error(`Error adding recipe with ID ${recipeId} to day with ID ${dayId}:`, error);
        throw error;
      }
    },

    /**
     * Remove a recipe from a day.
     * @param {number} dayId - The ID of the day.
     * @param {number} recipeId - The ID of the recipe to remove.
     */
    async removeRecipeFromDay(dayId, recipeId) {
      try {
        console.log(`Removing recipe with ID ${recipeId} from day with ID ${dayId}`);
        await axios.delete(`/days/${dayId}/recipes/${recipeId}`);
        console.log(`Recipe with ID ${recipeId} removed from day with ID ${dayId}`);
        await this.fetchWeeksInRange(this.weeks.length); // Refresh weeks to update changes
      } catch (error) {
        console.error(`Error removing recipe with ID ${recipeId} from day with ID ${dayId}:`, error);
        throw error;
      }
    },

    /**
     * Increment the portions for a recipe.
     * @param {number} recipeId - The ID of the recipe.
     */
    async incrementRecipePortions(recipeId) {
      try {
        console.log(`Incrementing portions for recipe ID ${recipeId}`);
        await axios.post(`/user-specific-recipes/${recipeId}/increment-portions`);
        console.log(`Portions incremented for recipe ID ${recipeId}`);
        await this.fetchWeeksInRange(this.weeks.length); // Refresh weeks to update changes
      } catch (error) {
        console.error(`Error incrementing portions for recipe ID ${recipeId}:`, error);
        throw error;
      }
    },

    /**
     * Decrement the portions for a recipe.
     * @param {number} recipeId - The ID of the recipe.
     */
    async decrementRecipePortions(recipeId) {
      try {
        console.log(`Decrementing portions for recipe ID ${recipeId}`);
        await axios.post(`/user-specific-recipes/${recipeId}/decrement-portions`);
        console.log(`Portions decremented for recipe ID ${recipeId}`);
        await this.fetchWeeksInRange(this.weeks.length); // Refresh weeks to update changes
      } catch (error) {
        console.error(`Error decrementing portions for recipe ID ${recipeId}:`, error);
        throw error;
      }
    },
    async handleWeekTransition() {
      const today = new Date();

      // Get the end date of the last week in the current range
      const lastWeek = this.weeks[this.weeks.length - 1];
      const lastWeekEndDate = new Date(lastWeek.endDate);

      // Check if today is beyond the last week's end date
      if (today > lastWeekEndDate) {
        console.log('Current week is outdated. Creating a new week...');

        // Create a new week on the backend
        const nextWeekStartDate = new Date(lastWeekEndDate);
        nextWeekStartDate.setDate(nextWeekStartDate.getDate() + 1); // Day after last week's end date
        const nextWeekEndDate = new Date(nextWeekStartDate);
        nextWeekEndDate.setDate(nextWeekStartDate.getDate() + 6); // 7 days in a week

        await this.createWeek({
          userId: getUserId(),
          startDate: nextWeekStartDate,
          endDate: nextWeekEndDate,
          days: [], // Backend should populate days automatically
        });

        // Fetch updated weeks (next 2 weeks, including the new one)
        await this.fetchWeeksInRange(2);
      }
    },

    async createWeek(weekRequest) {
      try {
        const response = await axios.post('/weeks', weekRequest);
        console.log('New week created:', response.data);
      } catch (error) {
        console.error('Error creating week:', error);
        throw error;
      }
    },
  },
  getters: {
    /**
     * Get all days of the current week.
     */
    currentDays(state) {
      return state.weeks[state.currentWeekIndex]?.days || [];
    },

    /**
     * Get all recipes for a specific day by its ID.
     */
    getRecipesForDay: (state) => (dayId) => {
      const day = state.weeks[state.currentWeekIndex]?.days.find((d) => d.id === dayId);
      return day ? day.userSpecificRecipes || [] : [];
    },
    getShoppingList(state) {
      // Get days from current week, or empty array if no data exists
      const days = state.weeks[state.currentWeekIndex]?.days || [];
      // Object to store aggregated ingredients
      const aggregated = {};
     
      // Iterate through each day
      days.forEach((day) => {
        // Iterate through user specific recipes for each day
        day.userSpecificRecipes?.forEach((usr) => {
          const { recipeData, portions } = usr;
          // Skip if recipe data or ingredients are missing
          if (!recipeData || !recipeData.ingredients) return;
     
          // Process each ingredient in the recipe
          recipeData.ingredients.forEach((ingredient) => {
            // Create unique key for each ingredient based on name and unit
            const key = ingredient.name + '_' + ingredient.unit;
            // If this ingredient hasn't been added yet, initialize it
            if (!aggregated[key]) {
              aggregated[key] = {
                name: ingredient.name,
                unit: ingredient.unit,
                amount: 0,
                ingredientType: ingredient.ingredientType || 'NONE'
              };
            }
            // Add to total amount (ingredient amount * recipe portions)
            aggregated[key].amount += ingredient.amount * portions;
          });
        });
      });
     
      // Convert to array and sort by ingredient type
      return Object.values(aggregated).sort((a, b) => {
        // 'NONE' category should always be at the end
        if (a.ingredientType === 'NONE') return 1;
        if (b.ingredientType === 'NONE') return -1;
        // Sort other categories alphabetically
        return a.ingredientType.localeCompare(b.ingredientType);
      });
     }
  },
});
