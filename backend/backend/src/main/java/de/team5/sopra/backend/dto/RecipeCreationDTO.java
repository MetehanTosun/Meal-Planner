package de.team5.sopra.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class RecipeCreationDTO {

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String getFoodType() {
		return foodType;
	}

	public void setFoodType(String foodType) {
		this.foodType = foodType;
	}

	public List<String> getInstructions() {
		return instructions;
	}

	public void setInstructions(List<String> instructions) {
		this.instructions = instructions;
	}

	public List<IngredientDetailDTO> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<IngredientDetailDTO> ingredients) {
		this.ingredients = ingredients;
	}
}

