package de.team5.sopra.backend.service;

import de.team5.sopra.backend.models.Ingredient;
import de.team5.sopra.backend.models.Recipe;

import de.team5.sopra.backend.models.User;
import de.team5.sopra.backend.repository.RecipeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
@Transactional
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    /**
     * Alle Recipes aus der DB holen
     */
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    /**
     * Einzelnes Recipe per ID
     */
    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Recipe with the ID: " + id + " wasn't found."
                ));
    }

    /**
     * Gibt die Ingredients eines Rezepts zurück.
     */
    public List<Ingredient> getAllIngredientsForRecipe(Long recipeId) {
        Recipe recipe = getRecipeById(recipeId);
        return recipe.getIngredients();
    }

    /**
     * Rezept inklusive Zutaten erstellen.
     * 'cascade = ALL' im Recipe-Entity sorgt dafür, dass die Ingredients automatisch gespeichert werden.
     */
    public Recipe createRecipe(Recipe recipe) {
        if (recipe.getCreator() == null) {
            throw new IllegalArgumentException("Recipe must have a creator");
        }
        return recipeRepository.save(recipe);
    }

    /**
     * Aktualisiert ein existierendes Recipe.
     * Falls es das Rezept nicht gibt, fliegt 404 NOT_FOUND (ResponseStatusException).
     */
    public Recipe updateRecipe(Long id, Recipe requestBody) {
        Recipe existing = getRecipeById(id);

        existing.setName(requestBody.getName());
        existing.setFoodtype(requestBody.getFoodtype());
        existing.setTime(requestBody.getTime());
        existing.setInstructions(requestBody.getInstructions());


        existing.getIngredients().clear();

        if (requestBody.getIngredients() != null) {
            for (Ingredient ing : requestBody.getIngredients()) {
                existing.addIngredient(ing);
            }
        }

        return recipeRepository.save(existing);
    }

    /**
     * Löscht ein Recipe samt Ingredients (wegen orphanRemoval=true)
     */
    public void deleteRecipeById(Long id) {
        // Check, ob existiert
        getRecipeById(id);
        recipeRepository.deleteById(id);
    }

    /**
     * Löscht eine einzelne Ingredient aus einem Recipe anhand des Ingredient-Namens.
     * (Oder man macht es ID-basiert, was meist besser ist.)
     */
    public void deleteIngredient(Long recipeId, String ingredientName) {
        Recipe recipe = getRecipeById(recipeId);

        Ingredient found = null;
        for (Ingredient ingr : recipe.getIngredients()) {
            if (ingr.getName().equalsIgnoreCase(ingredientName)) {
                found = ingr;
                break;
            }
        }

        if (found == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Ingredient '" + ingredientName + "' not found in recipe " + recipeId);
        }

        recipe.removeIngredient(found);
    }

    public List<String> getInstructions(Long recipeId) {
        Recipe recipe = getRecipeById(recipeId);
        return recipe.getInstructions();
    }

    public List<Recipe> getAllRecipesByUser(User user) {
        return recipeRepository.findByCreator(user);
    }

}
