// src/classes/IngredientTypes.js
export const INGREDIENT_TYPES = {
    NONE: { value: 'NONE', label: 'Keine Kategorie', color: '#808080' },             // Grey for uncategorized items
    VEGETABLES: { value: 'VEGETABLES', label: 'Gemüse', color: '#4CAF50' },          // Green for vegetables
    FRUITS: { value: 'FRUITS', label: 'Obst', color: '#FF5722' },                    // Orange-red for fruits
    MEAT_FISH: { value: 'MEAT_FISH', label: 'Fisch & Fleisch', color: '#E91E63' },   // Pink-red for meat and fish
    DAIRY: { value: 'DAIRY', label: 'Milchprodukte', color: '#03A9F4' },             // Light blue for dairy products
    GRAINS: { value: 'GRAINS', label: 'Getreide & Backwaren', color: '#8D6E63' },    // Beige/brown for grains and baked goods
    SPICES: { value: 'SPICES', label: 'Gewürze', color: '#9C27B0' },                 // Purple for spices
    EGG_PRODUCTS: { value: 'EGG_PRODUCTS', label: 'Eiprodukte', color: '#FFC107' },  // Yellow for egg products
    NUTS_SEEDS: { value: 'NUTS_SEEDS', label: 'Nüsse & Samen', color: '#795548' },   // Dark brown for nuts and seeds
    SPREADS_SAUCES: { value: 'SPREADS_SAUCES', label: 'Aufstriche & Saucen', color: '#FF9800' }, // Orange for spreads and sauces
    OTHERS: { value: 'OTHERS', label: 'Anderes', color: '#607D8B' }                  // Grey-blue for other items
 }