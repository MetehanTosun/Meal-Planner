package de.team5.sopra.backend.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "recipes")
@Getter
@Setter
public class Recipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String name;

    @Min(0)
    private int time;

    @ElementCollection
    @CollectionTable(
            name = "recipe_ingredients",
            joinColumns = @JoinColumn(name = "recipe_id") // Fremdschlüssel zu recipes.id
    )
    private List<String> ingredients = new ArrayList<>();

    // Mapping für instructions
    @ElementCollection
    @CollectionTable(
            name = "recipe_instructions",
            joinColumns = @JoinColumn(name = "recipe_id") // Fremdschlüssel zu recipes.id
    )
    private List<String> instructions = new ArrayList<>();

    @NotNull(message = "Food type cant be null")
    @Enumerated(EnumType.STRING)
    private Recipe.FoodType foodtype;

    enum FoodType {
        VEGAN,
        VEGETARIAN,
        MEAT
    }

    @ManyToMany(mappedBy = "recipes")
    private List<Day> days = new ArrayList<>();

    public Recipe(){}

    public Recipe(String name, FoodType foodtype, List<String> ingredients, List<String> instructions, int time) {
        this.name = name;
        this.foodtype = foodtype;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.time = time;
    }
}
