package de.team5.sopra.backend.controller;

import de.team5.sopra.backend.dto.RecipeDTO;
import de.team5.sopra.backend.exception.ForbiddenException;
import de.team5.sopra.backend.models.Recipe;
import de.team5.sopra.backend.models.User;
import de.team5.sopra.backend.service.RecipeService;
import de.team5.sopra.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import de.team5.sopra.backend.models.Ingredient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllRecipes(@RequestHeader("User-Id") Long userId) {
        try {
            User user = userService.getUserById(userId);
            List<Recipe> recipes = recipeService.getAllRecipesByUser(user);
            return ResponseEntity.ok(recipes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * GET /recipes/{id} : Rezept per ID
     */
    @GetMapping("/{id}")
    public Recipe getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeById(id);
    }

    /**
     * POST /recipes : Neues Rezept anlegen
     * Erwartet im RequestBody ein JSON, das auch "ingredients" als Array von Ingredient-Objekten enthält.
     */
    @PostMapping
    public ResponseEntity<?> createRecipe(@RequestHeader("User-Id") Long userId,
                                          @RequestBody Recipe recipe) {
        try {
            User user = userService.getUserById(userId);
            recipe.setCreator(user);
            Recipe created = recipeService.createRecipe(recipe);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * PUT /recipes/{id} : Rezept aktualisieren
     */
    @PutMapping("/{id}")
    public Recipe updateRecipe(@PathVariable Long id, @RequestBody Recipe requestBody) {
        return recipeService.updateRecipe(id, requestBody);
    }

    /**
     * DELETE /recipes/{id} : Rezept löschen
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        Recipe recipe = recipeService.getRecipeById(id);

        if (!recipe.getCreator().equals(currentUser)) {
            throw new ForbiddenException("You can only delete your own recipes");
        }

        recipeService.deleteRecipeById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /recipes/{id}/ingredients : Zutaten eines Rezepts abrufen
     */
    @GetMapping("/{id}/ingredients")
    public List<Ingredient> getAllIngredients(@PathVariable Long id) {
        return recipeService.getAllIngredientsForRecipe(id);
    }

    /**
     * DELETE /recipes/{recipeId}/ingredients/{ingredientName}
     * Löscht eine einzelne Ingredient anhand des Namens
     * (in der Praxis besser: /{ingredientId} ID-basiert).
     */
    @DeleteMapping("/{recipeId}/ingredients/{ingredientName}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long recipeId,
                                                 @PathVariable String ingredientName) {
        recipeService.deleteIngredient(recipeId, ingredientName);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /recipes/{id}/instructions : Instructions eines Rezepts abrufen
     */
    @GetMapping("/{id}/instructions")
    public List<String> getInstructions(@PathVariable Long id) {
        return recipeService.getInstructions(id);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.getUserByUsername(username);
    }

}
