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
	private Map<String, Integer> weeklyCookingTimes;
	private int averageTimePerRecipe;
	private Map<String, Integer> recipeFrequency;

	public UserStatisticsDTO() {}

	public UserStatisticsDTO(
			int totalTime,
			Map<FoodType, Integer> foodTypeCounts,
			Map<String, Integer> ingredientCounts,
			int recipeCount,
			Map<String, Integer> weeklyCookingTimes,
			int averageTimePerRecipe,
			Map<String, Integer> recipeFrequency) {
		this.totalTime = totalTime;
		this.foodTypeCounts = foodTypeCounts;
		this.ingredientCounts = ingredientCounts;
		this.recipeCount = recipeCount;
		this.weeklyCookingTimes = weeklyCookingTimes;
		this.averageTimePerRecipe = averageTimePerRecipe;
		this.recipeFrequency = recipeFrequency;
	}
}
