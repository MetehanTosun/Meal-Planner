package de.team5.sopra.backend.service;

import de.team5.sopra.backend.models.Recipe;
import de.team5.sopra.backend.repository.RecipeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public Recipe getRecipeById(Long id) {
        Optional<Recipe> searchedRecipe = recipeRepository.findById(id);
        if (searchedRecipe.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The day with the ID: " + id + " wasn't found.");
        }
        return searchedRecipe.get();
    }

    public List<String> getAllIngredients(Recipe recipe) {
        if (recipe.getIngredients().isEmpty()) {
            return new ArrayList<>();
        }
        return recipe.getIngredients();
    }

    @Transactional
    public Recipe createRecipe(Recipe recipe) {
        // Speichern des Rezepts in der Datenbank
        Recipe savedRecipe = recipeRepository.save(recipe);

        return savedRecipe;
    }
    public void deleteRecipeById(Long id) {
        recipeRepository.deleteById(id);
    }

    public void deleteIngredient(String ingredient) {
        Recipe recipe = new Recipe();
        if (ingredient != null) {
            List<String> ingredients = recipe.getIngredients();
            // Entferne das Ingredient, falls es existiert
            if (ingredients.contains(ingredient)) {
                ingredients.remove(ingredient);
            }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The ingredient " + ingredient + " was not found.");
        }
    }

    public List<String> getInstructions() {
        Recipe recipe = new Recipe();
        List<String> instructions = recipe.getInstructions();
        return instructions;
    }
}
