package de.team5.sopra.backend.repository.general;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import de.team5.sopra.backend.models.Ingredient;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

}