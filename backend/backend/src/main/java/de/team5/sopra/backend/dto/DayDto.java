package de.team5.sopra.backend.dto;

import java.util.List;

import de.team5.sopra.backend.models.Recipe;

/*
 * DTO to secure that the name at creation(POST) is a valid ENUM for the Weekday
 */

public class DayDto {
    private String name; 
    private List<Recipe> recipes;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }
}
