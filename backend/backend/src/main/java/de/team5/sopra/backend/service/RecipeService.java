package de.team5.sopra.backend.service;

import de.team5.sopra.backend.dto.ShareRecipeDTO;
import de.team5.sopra.backend.models.Ingredient;
import de.team5.sopra.backend.models.Recipe;

import de.team5.sopra.backend.models.User;
import de.team5.sopra.backend.repository.RecipeRepository;
import de.team5.sopra.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;
	@Autowired
	private UserRepository userRepository;

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
        if (recipe.getUser() == null) {
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
        existing.setFoodType(requestBody.getFoodType());
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
        if(!recipeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found!");
        }
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
        return recipeRepository.findByUser(user);
    }

    public Recipe toggleFavorite(Long recipeId, Long userId) {
        Recipe recipe = getRecipeById(recipeId);

        if (recipe.isFavoriteByUser(userId)) {
            recipe.removeFavoriteByUser(userId);
        } else {
            recipe.addFavoriteByUser(userId);
        }

        return recipeRepository.save(recipe);  // Speichern und zurückgeben
    }

    /**
     * Creates a copy of an original recipe, by getting recipe data via the id and then creating a new recipe
     * and its ingredients.
     *
     * @param shareRecipeDTO includes recipeId of to be shared recipe and a userName of the receiver.
     */
    public void shareRecipe(ShareRecipeDTO shareRecipeDTO) {
        if(shareRecipeDTO.getReceiverName() == null){
            throw new IllegalArgumentException("The request needs to contain a non null receiver name.");
        }
        Optional<User> receiver = userRepository.findByUsername(shareRecipeDTO.getReceiverName());
        if(receiver.isEmpty()) {
            throw new IllegalArgumentException("The request needs to contain a existing user.");
        }
        Recipe originalRecipe = getRecipeById(shareRecipeDTO.getRecipeId());
        Recipe copyRecipe = new Recipe();
        copyRecipe.setName(originalRecipe.getName());
        copyRecipe.setFoodType(originalRecipe.getFoodType());
        copyRecipe.setTime(originalRecipe.getTime());
        copyRecipe.setInstructions(new ArrayList<>(originalRecipe.getInstructions()));
        copyRecipe.setUser(receiver.get());

        List<Ingredient> copiedIngredients = new ArrayList<>();
        for (Ingredient originalIngredient : originalRecipe.getIngredients()) {
            Ingredient copiedIngredient = new Ingredient();
            copiedIngredient.setName(originalIngredient.getName());
            copiedIngredient.setAmount(originalIngredient.getAmount());
            copiedIngredient.setUnit(originalIngredient.getUnit());
            copiedIngredient.setRecipe(copyRecipe);
            copiedIngredients.add(copiedIngredient);
        }
        copyRecipe.setIngredients(copiedIngredients);
        recipeRepository.save(copyRecipe);
    }
}
