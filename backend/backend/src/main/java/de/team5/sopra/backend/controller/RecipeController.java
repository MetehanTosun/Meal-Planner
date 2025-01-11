package de.team5.sopra.backend.controller;


import de.team5.sopra.backend.dto.RecipeCreationDTO;
import de.team5.sopra.backend.dto.RecipeDTO;
import de.team5.sopra.backend.dto.ShareRecipeDTO;
import de.team5.sopra.backend.exception.ForbiddenException;
import de.team5.sopra.backend.models.Recipe;
import de.team5.sopra.backend.models.User;
import de.team5.sopra.backend.service.RecipeService;
import de.team5.sopra.backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import de.team5.sopra.backend.models.Ingredient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class RecipeController {

	private final RecipeService recipeService;

	private final UserService userService;

	private static final Logger log = LoggerFactory.getLogger(RecipeController.class);

	/**
	 * GET /recipes : Liste aller Rezepte
	 */
	@GetMapping
	public List<Recipe> getAllRecipes(@RequestHeader("User-Id") Long userId) {
		User currentUser = userService.getUserById(userId);
		return recipeService.getAllRecipesByUser(currentUser);
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
	public ResponseEntity<RecipeDTO> createRecipe(@RequestHeader("User-Id") Long userId, @Valid @RequestBody Recipe recipeRequest) {
		try {
			User currentUser = userService.getUserById(userId);
			recipeRequest.setUser(currentUser);
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

		if (!recipe.getUser().equals(currentUser)) {
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

	/**
	 * @return
	 */
	@PutMapping("/share")
	public ResponseEntity<?> shareRecipe(@RequestBody ShareRecipeDTO shareRecipeDTO) {
		try{
			recipeService.shareRecipe(shareRecipeDTO);
			return ResponseEntity.status(HttpStatus.CREATED).body("Succesfully Added Recipe to new User");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}


	private User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated() ||
				"anonymousUser".equals(authentication.getPrincipal())) {
			throw new IllegalStateException("No authenticated user found");
		}


		if (authentication.getPrincipal() instanceof String) {
			String username = (String) authentication.getPrincipal();
			return userService.getUserByUsername(username); // Use userService here
		}

		// Check if the Principal is a UserDetails implementation
		if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
			String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
			return userService.getUserByUsername(username); // Use userService here
		}

		throw new IllegalStateException("Unexpected Principal type: " + authentication.getPrincipal().getClass().getName());
	}

	@PutMapping("/{id}/toggle-favorite")
	public ResponseEntity<RecipeDTO> toggleFavorite(@RequestHeader("User-Id") Long userId, @PathVariable Long id) {
		try {
			if (!userService.existsById(userId)) {
				throw new EntityNotFoundException("User not found with id: " + userId);
			}

			Recipe recipe = recipeService.toggleFavorite(id, userId);
			return ResponseEntity.ok(RecipeDTO.fromRecipe(recipe));
		} catch (EntityNotFoundException e) {
			log.error("User not found", e);
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(null);
		} catch (Exception e) {
			log.error("Error toggling favorite", e);
			throw e;
		}
	}

	@GetMapping("/{id}/is-favorite")
	public ResponseEntity<Boolean> isFavorite(@PathVariable Long id) {
		try {
			Long userId = getCurrentUser().getId();
			Recipe recipe = recipeService.getRecipeById(id);
			return ResponseEntity.ok(recipe.isFavoriteByUser(userId));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
