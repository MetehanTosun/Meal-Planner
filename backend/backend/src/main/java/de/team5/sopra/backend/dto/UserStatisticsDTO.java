package de.team5.sopra.backend.dto;

import de.team5.sopra.backend.models.enums.FoodType;
import lombok.Getter;

import java.util.Map;

@Getter
public class UserStatisticsDTO {

	private int totalTime;
	private Map<FoodType, Integer> foodTypeCounts;
	private Map<String, Integer> ingredientCounts;
	private int recipeCount;

	public UserStatisticsDTO() {}

	public UserStatisticsDTO(int totalTime, Map<FoodType, Integer> foodTypeCounts, Map<String, Integer> ingredientCounts, int recipeCount) {
		this.totalTime = totalTime;
		this.foodTypeCounts = foodTypeCounts;
		this.ingredientCounts = ingredientCounts;
		this.recipeCount = recipeCount;
	}

}
