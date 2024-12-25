package de.team5.sopra.backend.dto;

import de.team5.sopra.backend.models.Ingredient;
import de.team5.sopra.backend.models.Recipe;
import de.team5.sopra.backend.models.enums.FoodType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class RecipeDTO {
    private Long id;
    private String name;
    private int time;
    private FoodType foodtype;
    private List<IngredientDetailDTO> ingredients;  // Hier ändern wir von List<Ingredient> zu List<IngredientDetailDTO>

    public static RecipeDTO fromRecipe(Recipe recipe) {
        RecipeDTO dto = new RecipeDTO();
        dto.setId(recipe.getId());
        dto.setName(recipe.getName());
        dto.setTime(recipe.getTime());
        dto.setFoodtype(recipe.getFoodtype());

        // Konvertierung der Ingredients zu DTOs
        List<IngredientDetailDTO> ingredientDTOs = new ArrayList<>();
        List<Ingredient> ingredients = recipe.getIngredients();

        for (Ingredient ingredient : ingredients) {
            IngredientDetailDTO ingredientDTO = new IngredientDetailDTO();
            ingredientDTO.setName(ingredient.getName());
            ingredientDTO.setAmount(ingredient.getAmount());
            ingredientDTO.setUnit(ingredient.getUnit().toString());
            ingredientDTOs.add(ingredientDTO);
        }

        dto.setIngredients(ingredientDTOs);
        return dto;
    }
}