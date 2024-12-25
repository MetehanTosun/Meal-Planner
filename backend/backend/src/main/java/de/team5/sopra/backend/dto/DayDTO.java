package de.team5.sopra.backend.dto;

import de.team5.sopra.backend.models.Day;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class DayDTO {
    private Long id;
    private Date date;
    private List<RecipeDTO> recipes;

    public static DayDTO fromDay(Day day) {
        DayDTO dto = new DayDTO();
        dto.setId(day.getId());
        dto.setDate(day.getDate());
        dto.setRecipes(day.getRecipes().stream()
                .map(RecipeDTO::fromRecipe)
                .collect(Collectors.toList()));
        return dto;
    }
}
