package de.team5.sopra.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiRecipeDTO {
    private String name;
    private List<String> ingredients;
    private double time;
    private String nutrition;
    private List<String> diet;

    public ApiRecipeDTO(){}

    public ApiRecipeDTO(String name, List<String> ingredients, double time, String nutrition, List<String> diet) {
        this.name = name;
        this.ingredients = ingredients;
        this.time = time;
        this.nutrition = nutrition;
        this.diet = (diet != null && !diet.isEmpty()) ? diet : List.of("none");
    }
}
