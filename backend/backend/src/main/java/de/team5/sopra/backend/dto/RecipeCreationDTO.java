package de.team5.sopra.backend.dto;

import de.team5.sopra.backend.models.User;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RecipeCreationDTO {

	@NotNull
	private User user;

	@NotBlank(message = "Recipe name cannot be empty")
	private String name;

	@Min(value = 1, message = "Time must be at least 1 minute")
	private int time;

	@NotNull(message = "Food type must be specified")
	private String foodType;

	@NotNull(message = "Instructions cannot be null")
	private List<String> instructions = new ArrayList<>();

	@NotNull(message = "Ingredients cannot be null")
	private List<IngredientDetailDTO> ingredients = new ArrayList<>();

}

