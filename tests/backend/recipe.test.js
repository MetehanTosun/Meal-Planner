const axios = require('axios');
const BASE_URL = 'http://localhost:8080';

class Recipe {
    constructor(name, foodType, ingredients, instructions, time) {
        this.name = name;
        this.foodType = foodType;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.time = time;
    }
}


describe('GET /recipes', () => {
    test('should return all recipes', async () => {
        const response = await axios.get(`${BASE_URL}/recipes`);
        const recipes = response.data;

        // Assert the expected behavior based on the API response
        expect(recipes.length).toBeGreaterThan(0);
        // ... other assertions
    });
});


describe('API Tests', () => {
    it('should create a new recipe', async () => {
        const newRecipeData = {
            "name": "Tomato Soup",
            "time": 20,
            "ingredients": [
                "Tomatoes",
                "Garlic",
                "Basil",
                "Olive Oil"
            ],
            "instructions": [
                "Chop tomatoes",
                "Cook with garlic",
                "Blend and serve"
            ],
            "foodtype": "VEGAN"
        };

        const response = await axios.post(`${BASE_URL}/recipes`, newRecipeData);

        expect(response.status).toBe(201);
        expect(response.data).toEqual({
            "name": "Tomato Soup",
            "time": 20,
            "ingredients": [
                "Tomatoes",
                "Garlic",
                "Basil",
                "Olive Oil"
            ],
            "instructions": [
                "Chop tomatoes",
                "Cook with garlic",
                "Blend and serve"
            ],
            "foodtype": "VEGAN",
            "id": expect.any(Number) // Expect an id to be present
        });
    });
});

