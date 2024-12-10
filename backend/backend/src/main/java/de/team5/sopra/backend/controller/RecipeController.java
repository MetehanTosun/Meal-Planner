package de.team5.sopra.backend.controller;

import de.team5.sopra.backend.models.Recipe;
import de.team5.sopra.backend.repository.RecipeRepository;
import de.team5.sopra.backend.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private RecipeRepository recipeRepository;

    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    @GetMapping("/{id}")
    public Recipe getRecipeById(@PathVariable Long id) {

        Recipe recipe = recipeService.getRecipeById(id);
        return recipe;
    }

    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@Valid @RequestBody Recipe recipeRequest) {
        System.out.println("bla bla ");
        Recipe createdRecipe = recipeService.createRecipe(recipeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecipe);

    }

    @PutMapping("/{id}")
    public Recipe updateRecipe(@PathVariable Long id, @RequestBody Recipe requestBody) {

        Optional<Recipe> existingRecipe = recipeRepository.findById(id);
        if (existingRecipe.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe with ID " + id + " not found");
        }

        Recipe recipe = existingRecipe.get();

        recipe.setName(requestBody.getName());
        recipe.setFoodtype(requestBody.getFoodtype());
        recipe.setIngredients(requestBody.getIngredients());
        recipe.setInstructions(requestBody.getInstructions());
        recipe.setTime(requestBody.getTime());

        recipeRepository.save(recipe);

        return recipe;
    }

    @DeleteMapping("/{id}")
    public Recipe deleteRecipe(@PathVariable Long id) {

        Optional<Recipe> existingRecipe = recipeRepository.findById(id);
        if (existingRecipe.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe with ID " + id + " not found");
        }

        Recipe recipeToDelete = existingRecipe.get();
        recipeRepository.deleteById(id);

        return recipeToDelete;
    }
}
