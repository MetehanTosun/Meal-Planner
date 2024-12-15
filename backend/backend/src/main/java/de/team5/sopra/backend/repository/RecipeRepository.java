package de.team5.sopra.backend.repository;

import de.team5.sopra.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import de.team5.sopra.backend.models.Recipe;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByCreator(User user);

}
