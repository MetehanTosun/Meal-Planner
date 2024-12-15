package de.team5.sopra.backend.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.team5.sopra.backend.models.enums.FoodType;
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

    /**
     * Anstelle der ElementCollection-Liste von Strings jetzt eine
     * One-to-Many-Beziehung zur Ingredient-Entity.
     * CascadeType.ALL erlaubt es z. B. neue Zutaten gleich
     * mit dem Rezept zu persistieren bzw. beim Löschen auch
     * die zugehörigen Zutaten zu löschen (orphanRemoval = true).
     */
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    //CRITICAL
    @JsonManagedReference
    private List<Ingredient> ingredients = new ArrayList<>();


    @ElementCollection
    @CollectionTable(
            name = "recipe_instructions",
            joinColumns = @JoinColumn(name = "recipe_id")
    )
    private List<String> instructions = new ArrayList<>();

    @NotNull(message = "Food type cant be null")
    @Enumerated(EnumType.STRING)
    private FoodType foodtype;



    @JsonIgnore
    @ManyToMany(mappedBy = "recipes")
    private List<Day> days = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User creator;

    public Recipe(){}

    public Recipe(String name, FoodType foodtype, List<Ingredient> ingredients, List<String> instructions, int time) {
        this.name = name;
        this.foodtype = foodtype;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.time = time;
    }

    public void addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        ingredient.setRecipe(this);
    }

    public void removeIngredient(Ingredient ingredient) {
        this.ingredients.remove(ingredient);
        ingredient.setRecipe(null);
    }
}
