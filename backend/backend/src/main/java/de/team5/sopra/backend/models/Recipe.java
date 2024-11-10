package de.team5.sopra.backend.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class Recipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Foodtype foodtype;

    enum Foodtype{
        VEGAN,
        VEGETARIAN,
        MEAT
    }

    @ManyToMany(mappedBy = "dayRecipes")
    private List<Day> days = new ArrayList<>();

    private String instructions;

    public Recipe(){}

    public Recipe(String name, Foodtype foodtype, String instructions) {
        this.name = name;
        this.foodtype = foodtype;
        this.instructions = instructions;
    }
    // Getters and setters
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Foodtype getFoodtype() {
        return foodtype;
    }

    public void setFoodtype(Foodtype foodtype) {
        this.foodtype = foodtype;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
