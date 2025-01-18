/*
 Author: Ethan Banovic
 Description: This file is used in all vue components to
 dynamically change the design once logged in and enable functionality.
*/
import { computed } from 'vue'

const USER_STORAGE_KEY = 'userId'

export const getUserId = () => {
  return localStorage.getItem(USER_STORAGE_KEY)
};

export const setUserId = (userId) => {
  localStorage.setItem(USER_STORAGE_KEY, userId)
};
export const clearUserId = () => {
  localStorage.removeItem(USER_STORAGE_KEY)
};
export const isAuthenticated= computed(() => {
  return localStorage.getItem('userId') !== null
});

