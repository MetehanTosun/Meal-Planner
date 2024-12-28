package de.team5.sopra.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.team5.sopra.backend.dto.RecipeDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_recipe")
public class UserSpecificRecipe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JsonBackReference("recipe-userSpecificRecipe")
	private Recipe recipe;

	@ManyToOne(fetch = FetchType.EAGER)
	@JsonBackReference("day-userSpecificRecipe")
	private Day day;

	@Min(1)
	private int portions;


	@JsonProperty("recipeData")
	public RecipeDTO getRecipeDTO() {
		return RecipeDTO.fromRecipe(recipe);
	}
}
