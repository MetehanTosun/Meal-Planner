package de.team5.sopra.backend.controller;


import de.team5.sopra.backend.dto.IngredientDetailDTO;
import de.team5.sopra.backend.dto.RecipeDTO;
import de.team5.sopra.backend.exception.ForbiddenException;
import de.team5.sopra.backend.models.Recipe;
import de.team5.sopra.backend.models.User;
import de.team5.sopra.backend.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import de.team5.sopra.backend.models.Ingredient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class RecipeController {

    private final RecipeService recipeService;

    private static final Logger log = LoggerFactory.getLogger(RecipeController.class);

    /**
     * GET /recipes : Liste aller Rezepte
     */
    @GetMapping
    public List<RecipeDTO> getAllRecipes() {
        User currentUser = getCurrentUser();
        return recipeService.getAllRecipesByUser(currentUser).stream()
                .map(RecipeDTO::fromRecipe)
                .collect(Collectors.toList());
    }

    /**
     * GET /recipes/{id} : Rezept per ID
     */
    @GetMapping("/{id}")
    public RecipeDTO getRecipeById(@PathVariable Long id) {
        Recipe recipe = recipeService.getRecipeById(id);
        return RecipeDTO.fromRecipe(recipe);
    }

    /**
     * POST /recipes : Neues Rezept anlegen
     * Erwartet im RequestBody ein JSON, das auch "ingredients" als Array von Ingredient-Objekten enthält.
     */
    @PostMapping
    public ResponseEntity<RecipeDTO> createRecipe(@Valid @RequestBody Recipe recipeRequest) {
        try {
            User currentUser = getCurrentUser();
            recipeRequest.setCreator(currentUser);
            Recipe createdRecipe = recipeService.createRecipe(recipeRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(RecipeDTO.fromRecipe(createdRecipe));
        } catch (Exception e) {
            log.error("Error creating recipe", e);
            throw e;
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
    public List<IngredientDetailDTO> getAllIngredients(@PathVariable Long id) {
        List<Ingredient> ingredients = recipeService.getAllIngredientsForRecipe(id);
        List<IngredientDetailDTO> dtoList = new ArrayList<>();

        for (Ingredient ingredient : ingredients) {
            IngredientDetailDTO dto = new IngredientDetailDTO();
            dto.setName(ingredient.getName());
            dto.setAmount(ingredient.getAmount());
            dto.setUnit(ingredient.getUnit().toString());
            dtoList.add(dto);
        }

        return dtoList;
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
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
