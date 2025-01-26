package de.team5.sopra.backend.controller.general;

import de.team5.sopra.backend.models.UserSpecificRecipe;
import de.team5.sopra.backend.service.general.UserSpecificRecipeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-specific-recipes")
public class UserSpecificRecipeController {

	@Autowired
	private UserSpecificRecipeService userSpecificRecipeService;

	/**
	 * GET /user-specific-recipes
	 */
	@GetMapping
	public List<UserSpecificRecipe> getAllUserSpecificRecipes() {
		return userSpecificRecipeService.getAllUserSpecificRecipes();
	}

	/**
	 * GET /user-specific-recipes/{id}
	 */
	@GetMapping("/{id}")
	public UserSpecificRecipe getUserSpecificRecipeById(@PathVariable Long id) {
		return userSpecificRecipeService.getUserSpecificRecipeById(id);
	}

	/**
	 * POST /user-specific-recipes
	 * Erwartet ein JSON, mit recipeId, dayId und portions enthält.
	 */
	@PostMapping
	public ResponseEntity<UserSpecificRecipe> createUserSpecificRecipe(
			@RequestBody UserSpecificRecipe usrRequest) {
		UserSpecificRecipe created = userSpecificRecipeService.createUserSpecificRecipe(usrRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	/**
	 * PUT /user-specific-recipes/{id}
	 */
	@PutMapping("/{id}")
	public UserSpecificRecipe updateUserSpecificRecipe(@PathVariable Long id,
	                                                   @RequestBody UserSpecificRecipe usrRequest) {
		return userSpecificRecipeService.updateUserSpecificRecipe(id, usrRequest);
	}

	/**
	 * DELETE /user-specific-recipes/{id}
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUserSpecificRecipe(@PathVariable Long id) {
		userSpecificRecipeService.deleteUserSpecificRecipe(id);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Optional: GET /user-specific-recipes/day/{dayId}
	 * Listet alle USR´s für einen bestimmten Day auf.
	 */
	@GetMapping("/day/{dayId}")
	public List<UserSpecificRecipe> getAllByDay(@PathVariable Long dayId) {
		return userSpecificRecipeService.getAllByDay(dayId);
	}

	@PostMapping("/{id}/increment-portions")
	public UserSpecificRecipe incrementPortions(@PathVariable Long id) {
		return userSpecificRecipeService.incrementPortions(id);
	}

	@PostMapping("/{id}/decrement-portions")
	public ResponseEntity<?> decrementPortions(@PathVariable Long id) {
		try {
			UserSpecificRecipe updatedRecipe = userSpecificRecipeService.decrementPortions(id);
			return ResponseEntity.ok(updatedRecipe);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ein unerwarteter Fehler ist aufgetreten.");
		}
	}
}