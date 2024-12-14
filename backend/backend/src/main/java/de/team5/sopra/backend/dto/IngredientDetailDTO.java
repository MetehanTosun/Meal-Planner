package de.team5.sopra.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class IngredientDetailDTO {

	@NotBlank(message = "Ingredient name cannot be empty")
	private String name;

	@Min(value = 0, message = "Amount cannot be negative")
	private double amount;

	@NotNull(message = "Unit must be specified")
	private String unit;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}

