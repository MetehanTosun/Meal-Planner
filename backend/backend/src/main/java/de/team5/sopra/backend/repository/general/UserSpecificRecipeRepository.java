package de.team5.sopra.backend.repository.general;

import de.team5.sopra.backend.models.UserSpecificRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSpecificRecipeRepository extends JpaRepository<UserSpecificRecipe, Long> {

	List<UserSpecificRecipe> findByDayId(Long dayId);
	List<UserSpecificRecipe> findByRecipeId(Long recipeId);
}
