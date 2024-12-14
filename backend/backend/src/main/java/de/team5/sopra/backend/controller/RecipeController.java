package de.team5.sopra.backend.controller;


import de.team5.sopra.backend.models.Recipe;
import de.team5.sopra.backend.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import de.team5.sopra.backend.models.Ingredient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    /**
     * GET /recipes : Liste aller Rezepte
     */
    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
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
    public ResponseEntity<Recipe> createRecipe(@Valid @RequestBody Recipe recipeRequest) {
        Recipe createdRecipe = recipeService.createRecipe(recipeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecipe);
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

}
