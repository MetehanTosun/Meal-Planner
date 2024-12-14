package de.team5.sopra.backend.controller.general;

import de.team5.sopra.backend.models.Ingredient;
import de.team5.sopra.backend.service.general.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

	@Autowired
	private IngredientService ingredientService;

	/**
	 * Erstellt eine neue Zutat
	 */
	@PostMapping
	public ResponseEntity<?> createIngredient(@RequestBody Ingredient ingredient) {
		try {
			Ingredient saved = ingredientService.createIngredient(ingredient);
			return new ResponseEntity<>(saved, HttpStatus.CREATED);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Liefert alle Zutaten zurück
	 */
	@GetMapping
	public ResponseEntity<List<Ingredient>> getAllIngredients() {
		List<Ingredient> ingredients = ingredientService.getAllIngredients();
		return new ResponseEntity<>(ingredients, HttpStatus.OK);
	}

	/**
	 * GET /ingredients/{id}
	 * Liefert eine einzelne Zutat anhand der ID
	 */
	@GetMapping("/{id}")
	public ResponseEntity<?> getIngredient(@PathVariable Long id) {
		try {
			Ingredient ing = ingredientService.getIngredientById(id);
			return new ResponseEntity<>(ing, HttpStatus.OK);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * PUT /ingredients/{id}
	 * Aktualisiert eine bestehende Zutat
	 */
	@PutMapping("/{id}")
	public ResponseEntity<?> updateIngredient(@PathVariable Long id, @RequestBody Ingredient ingredient) {
		try {
			Ingredient updated = ingredientService.updateIngredient(id, ingredient);
			return new ResponseEntity<>(updated, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * DELETE /ingredients/{id}
	 * Löscht eine bestehende Zutat
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteIngredient(@PathVariable Long id) {
		try {
			ingredientService.deleteIngredient(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

}
