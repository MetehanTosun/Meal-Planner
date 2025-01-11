package de.team5.sopra.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.team5.sopra.backend.models.enums.FoodType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="ingredients")
@Getter
@Setter
public class Ingredient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotBlank(message = "Ingredient name cannot be empty")
	private String name;

	@Min(value = 0, message = "Ingredient amount cannot be negative")
	private double amount;

	@NotNull(message = "Ingredient unit must be specified")
	@Enumerated(EnumType.STRING)
	private Unit unit;

	public enum Unit {
		G,
		ML,
		STÜCK,
	}

	/**
	 * Beziehung zum Rezept (viele Zutaten gehören zu einem Rezept).
	 * Wichtig: In Ingredient weisen wir das Fremdschlüssel-Feld auf Recipe hin.
	 */
	@ManyToOne
	@JoinColumn(name = "recipe_id", nullable = false)
	//CRITICAL
	@JsonBackReference
	private Recipe recipe;

	public Ingredient() {

	}

	public Ingredient(String name, double amount, Unit unit, Recipe recipe) {
		this.name = name;
		this.amount = amount;
		this.unit = unit;
		this.recipe = recipe;
	}

	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
		if (!recipe.getIngredients().contains(this)) {
			recipe.getIngredients().add(this);
		}
	}
}
