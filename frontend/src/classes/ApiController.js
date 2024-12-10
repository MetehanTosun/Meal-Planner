import axios from "axios";

const backendURL = "http://localhost:8080";

export const fetchRecipes = async (query) => {
    try {
        const response = await axios.get(`${backendURL}/search-recipes`, {
            params: { query },
        });
        return response.data;
    } catch (error) {
        console.error("Error fetching recipes:", error);
        throw error;
    }
};
