package de.team5.sopra.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddRecipeToDayRequest {
	@NotNull
	private Long recipeId;

	@NotNull(message = "Warning the day id in recipe request is null!")
	private Long dayId;

	@Min(1)
	private Integer portions;
}

