package de.team5.sopra.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class DayRequest {
    private Date date;
    private Long weekId;
    private List<RecipeWithPortion> recipes;

    @Getter
    @Setter
    public static class RecipeWithPortion {
        private Long recipeId;
        private int portions;
    }

}