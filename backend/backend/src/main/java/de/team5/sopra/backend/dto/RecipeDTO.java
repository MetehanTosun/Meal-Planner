package de.team5.sopra.backend.dto;

import de.team5.sopra.backend.models.Ingredient;
import de.team5.sopra.backend.models.Recipe;
import de.team5.sopra.backend.models.enums.FoodType;
import lombok.Data;

import java.util.List;

@Data
public class RecipeDTO {
    private Long id;
    private String name;
    private int time;
    private FoodType foodtype;
    private List<Ingredient> ingredients;
    private Long creatorId;

    public static RecipeDTO fromRecipe(Recipe recipe) {
        RecipeDTO dto = new RecipeDTO();
        dto.setId(recipe.getId());
        dto.setName(recipe.getName());
        dto.setTime(recipe.getTime());
        dto.setFoodtype(recipe.getFoodType());
        dto.setIngredients(recipe.getIngredients());
        dto.setCreatorId(recipe.getUser().getId());
        return dto;
    }
}