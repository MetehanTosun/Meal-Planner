package de.team5.sopra.backend.dto;

import de.team5.sopra.backend.models.UserSpecificRecipe;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSpecificRecipeDTO {
	private Long id;
	private Long recipeId; // Include recipeId
	private int portions;

	public UserSpecificRecipeDTO(UserSpecificRecipe userSpecificRecipe) {
		this.id = userSpecificRecipe.getId();
		this.recipeId = userSpecificRecipe.getRecipe().getId();
		this.portions = userSpecificRecipe.getPortions();
	}
}
