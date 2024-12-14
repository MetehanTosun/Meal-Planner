package de.team5.sopra.backend.service.general;

import de.team5.sopra.backend.models.UserSpecificRecipe;
import de.team5.sopra.backend.repository.general.UserSpecificRecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserSpecificRecipeService {

	@Autowired
	private UserSpecificRecipeRepository userSpecificRecipeRepository;

	/**
	 * Liefert alle UserSpecificRecipe-Einträge
	 */
	public List<UserSpecificRecipe> getAllUserSpecificRecipes() {
		return userSpecificRecipeRepository.findAll();
	}

	/**
	 * Einzelnes UserSpecificRecipe per ID
	 */
	public UserSpecificRecipe getUserSpecificRecipeById(Long id) {
		Optional<UserSpecificRecipe> usr = userSpecificRecipeRepository.findById(id);
		if (usr.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"UserSpecificRecipe with ID " + id + " not found");
		}
		return usr.get();
	}

	/**
	 * Anlegen eines neuen UserSpecificRecipe-Eintrags
	 */
	public UserSpecificRecipe createUserSpecificRecipe(UserSpecificRecipe usr) {
		if (usr.getPortions() <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Portions must be greater than 0.");
		}
		return userSpecificRecipeRepository.save(usr);
	}

	/**
	 * Aktualisiert einen existierenden UserSpecificRecipe-Eintrag
	 */
	public UserSpecificRecipe updateUserSpecificRecipe(Long id, UserSpecificRecipe updated) {
		UserSpecificRecipe existing = getUserSpecificRecipeById(id);


		if (updated.getRecipe() != null) {
			existing.setRecipe(updated.getRecipe());
		}
		if (updated.getDay() != null) {
			existing.setDay(updated.getDay());
		}
		if (updated.getPortions() > 0) {
			existing.setPortions(updated.getPortions());
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Portions must be greater than 0.");
		}

		return userSpecificRecipeRepository.save(existing);
	}

	/**
	 * Löscht einen UserSpecificRecipe-Eintrag
	 */
	public void deleteUserSpecificRecipe(Long id) {
		// Check existence
		getUserSpecificRecipeById(id);
		userSpecificRecipeRepository.deleteById(id);
	}

	// Optional: weitere Suchmethoden
	public List<UserSpecificRecipe> getAllByDay(Long dayId) {
		return userSpecificRecipeRepository.findByDayId(dayId);
	}
}
