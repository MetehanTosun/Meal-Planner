package de.team5.sopra.backend.service.general;

import de.team5.sopra.backend.models.Ingredient;
import de.team5.sopra.backend.repository.general.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class IngredientService {

	@Autowired
	private IngredientRepository ingredientRepository;

	public Ingredient createIngredient(Ingredient ingredient) {

		if (ingredient.getName() == null || ingredient.getName().trim().isEmpty()) {
			throw new IllegalArgumentException("Name darf nicht leer sein!");
		}
		if (ingredient.getAmount() <= 0) {
			throw new IllegalArgumentException("Amount muss > 0 sein!");
		}

		return ingredientRepository.save(ingredient);
	}

	public List<Ingredient> getAllIngredients() {
		return ingredientRepository.findAll();
	}

	public Ingredient getIngredientById(Long id) {
		return ingredientRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Ingredient mit ID " + id + " wurde nicht gefunden."));
	}

	public Ingredient updateIngredient(Long id, Ingredient updated) {
		Ingredient existing = getIngredientById(id);

		if (updated.getName() != null && !updated.getName().trim().isEmpty()) {
			existing.setName(updated.getName());
		}
		if (updated.getAmount() > 0) {
			existing.setAmount(updated.getAmount());
		}
		if (updated.getUnit() != null) {
			existing.setUnit(updated.getUnit());
		}

		return ingredientRepository.save(existing);
	}

	public void deleteIngredient(Long id) {
		Ingredient ing = getIngredientById(id);
		ingredientRepository.delete(ing);
	}

}
