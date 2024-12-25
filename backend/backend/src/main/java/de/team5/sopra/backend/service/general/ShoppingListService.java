package de.team5.sopra.backend.service.general;

import de.team5.sopra.backend.models.Ingredient;
import de.team5.sopra.backend.models.Recipe;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShoppingListService {

    public Map<String, Double> generateShoppingList(List<Recipe> recipes) {
        Map<String, Double> shoppingList = new HashMap<>();

        for (Recipe recipe : recipes) {
            for (Ingredient ingredient : recipe.getIngredients()) {
                // Formatiere den Namen: Erster Buchstabe groß, Rest klein
                String formattedName = capitalizeWords(ingredient.getName());

                // Generiere den eindeutigen Schlüssel: "Zutatenname (Einheit)"
                String key = formattedName + " (" + ingredient.getUnit() + ")";

                // Falls der Schlüssel existiert, addiere die Menge
                shoppingList.put(
                        key,
                        shoppingList.getOrDefault(key, 0.0) + ingredient.getAmount()
                );
            }
        }

        return shoppingList;
    }

    public static String capitalizeWords(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        name = name.trim();
        String[] words = name.split("\\s+");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                capitalized.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return capitalized.toString().trim(); // Entferne überschüssige Leerzeichen
    }

}

