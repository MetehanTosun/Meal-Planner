const axios = require('axios');
const BASE_URL = 'http://localhost:8080';

describe('Ingredient API Tests', () => {

    // Beispiel: Endpoint POST /ingredients
    test('should create a new ingredient', async () => {
        const newIngredient = {
            name: 'Tomatoes',
            amount: 300.0,
            unit: 'G',
            recipeId: 1
        };

        // POST /ingredients
        const response = await axios.post(`${BASE_URL}/ingredients`, newIngredient);

        expect(response.status).toBe(201); // oder 200, je nachdem wie euer Backend das handhabt
        expect(response.data).toMatchObject({
            name: 'Tomatoes',
            amount: 300.0,
            unit: 'G',
            // id: expect.any(Number),  // Bei Bedarf prüfen, ob eine ID generiert wurde
        });
    });

    // Beispiel: Endpoint GET /ingredients
    test('should retrieve all ingredients', async () => {
        const response = await axios.get(`${BASE_URL}/ingredients`);
        expect(response.status).toBe(200);

        const ingredients = response.data;
        expect(Array.isArray(ingredients)).toBe(true);
        // Optional: weitere Assertions, z. B.
        // expect(ingredients.length).toBeGreaterThan(0);
    });

    // Optional: GET /ingredients/{id}, PUT /ingredients/{id}, DELETE /ingredients/{id} usw.
});
