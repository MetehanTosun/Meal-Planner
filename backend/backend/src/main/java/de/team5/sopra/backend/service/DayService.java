package de.team5.sopra.backend.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.team5.sopra.backend.dto.AddRecipeToDayRequest;
import de.team5.sopra.backend.dto.DayRequest;
import de.team5.sopra.backend.dto.UserSpecificRecipeDTO;
import de.team5.sopra.backend.models.UserSpecificRecipe;
import de.team5.sopra.backend.models.Week;
import de.team5.sopra.backend.repository.RecipeRepository;
import de.team5.sopra.backend.repository.WeekRepository;
import de.team5.sopra.backend.repository.general.UserSpecificRecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import de.team5.sopra.backend.models.Day;
import de.team5.sopra.backend.models.Recipe;
import de.team5.sopra.backend.repository.DayRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class DayService {

	@Autowired
	private DayRepository dayRepository;

	@Autowired
	private RecipeRepository recipeRepository;

	@Autowired
	private WeekRepository weekRepository;

	@Autowired
	private UserSpecificRecipeRepository userSpecificRecipeRepository;

	public List<Day> getAllDays() {
		return dayRepository.findAll();
	}

	public Day getDayById(Long id) {
		return dayRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Day not found with ID: " + id));
	}


	public Day saveDay(Day day) {
		return dayRepository.save(day);
	}

	public void deleteDay(Long id) {
		if (dayRepository.existsById(id)) {
			dayRepository.deleteById(id);
		} else {
			throw new EntityNotFoundException("Day not found with id: " + id);
		}
	}

	/**
	 * Adds a recipe to a day, by creating a userSpecificRecipe.
	 *
	 * Works by checking if the dayId and recipeId even exist in the database,
	 * then it creates a userSpecificRecipe and saves it in the repository. It also
	 * adds the USR to the days list of recipes and saves the day
	 *
	 * @param dayId
	 * @param recipeId
	 * @param portions
	 * @return  saved day entity
	 */
	public Day addRecipeToDayWithPortions(Long dayId, Long recipeId, Integer portions) {
		Day day = dayRepository.findById(dayId)
				.orElseThrow(() -> new EntityNotFoundException("Day not found with id: " + dayId));

		Recipe recipe = recipeRepository.findById(recipeId)
				.orElseThrow(() -> new EntityNotFoundException("Recipe not found with id: " + recipeId));

		if (portions == null || portions < 1) {
			portions = 1;
		}

		UserSpecificRecipe userSpecificRecipe = new UserSpecificRecipe();
		userSpecificRecipe.setDay(day);
		userSpecificRecipe.setRecipe(recipe);
		userSpecificRecipe.setPortions(portions);

		userSpecificRecipeRepository.save(userSpecificRecipe);
		day.getUserSpecificRecipes().add(userSpecificRecipe);

		return dayRepository.save(day);
	}

	/**
	 * Remove recipe all user specific recipes that are of the recipe with the given recipeId.
	 *
	 * It works by checking if the day exists, then collecting all the userSpecificRecipes, that
	 * are an entity of the recipe with the given recipeId. After that the matching usrs, are
	 * deleted from the day and also the other way around for clean database.
	 *
	 * @param dayId
	 * @param recipeId
	 */
	public void removeRecipeFromDay(Long dayId, Long recipeId) {
		Day day = dayRepository.findById(dayId)
				.orElseThrow(() -> new EntityNotFoundException("Day not found with id: " + dayId));

		// Might be unnecessary since a recipe is unique and can only increase portions within the same day, no time :/
		List<UserSpecificRecipe> matchingRecipes = new ArrayList<>();
		for (UserSpecificRecipe userSpecificRecipe : day.getUserSpecificRecipes()) {
			if (userSpecificRecipe.getRecipe().getId() == recipeId) {
				matchingRecipes.add(userSpecificRecipe);
			}
		}

		if (matchingRecipes.isEmpty()) {
			throw new IllegalArgumentException("Recipe with ID " + recipeId + " is not assigned to the day with ID " + dayId);
		}

		day.getUserSpecificRecipes().removeAll(matchingRecipes);
		matchingRecipes.forEach(userSpecificRecipeRepository::delete);
		dayRepository.save(day);
	}

	@Transactional
	public Day createDay(DayRequest dayRequest) {

		chechIfDateAndWeekIdAreNotNull(dayRequest);
		Week week = weekRepository.findById(dayRequest.getWeekId())
				.orElseThrow(() ->
						new EntityNotFoundException("Week not found with id: " + dayRequest.getWeekId())
				);

		List<DayRequest.RecipeWithPortion> recipeRequests =
				Optional.ofNullable(dayRequest.getRecipes()).orElse(Collections.emptyList());

		checkIfRecipesWithPortionsHaveCorrectValues(recipeRequests);

		Day day = new Day();
		day.setDate(dayRequest.getDate());
		day.setWeek(week);

		day.setUserSpecificRecipes(new ArrayList<>());
		day = dayRepository.save(day);

		for (DayRequest.RecipeWithPortion recipeWithPortion : recipeRequests) {
			Recipe recipe = recipeRepository.findById(recipeWithPortion.getRecipeId())
					.orElseThrow(() ->
							new EntityNotFoundException("Recipe not found with id: " + recipeWithPortion.getRecipeId())
					);

			UserSpecificRecipe usr = new UserSpecificRecipe();
			usr.setDay(day);
			usr.setRecipe(recipe);
			usr.setPortions(recipeWithPortion.getPortions());

			userSpecificRecipeRepository.save(usr);

			day.getUserSpecificRecipes().add(usr);
		}

		return dayRepository.save(day);
	}

	public Day updateDay(Long id, DayRequest dayRequest) {
		Day day = dayRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Day not found with id: " + id));

		if (dayRequest.getDate() != null) {
			day.setDate(dayRequest.getDate());
		}

		if (dayRequest.getWeekId() != null) {
			Week week = weekRepository.findById(dayRequest.getWeekId())
					.orElseThrow(() -> new EntityNotFoundException("Week not found with id: " + dayRequest.getWeekId()));
			day.setWeek(week);
		}

		if (dayRequest.getRecipes() != null && !dayRequest.getRecipes().isEmpty()) {
			day.getUserSpecificRecipes().clear();

			for (DayRequest.RecipeWithPortion recipeWithPortion : dayRequest.getRecipes()) {
				Recipe recipe = recipeRepository.findById(recipeWithPortion.getRecipeId())
						.orElseThrow(() -> new EntityNotFoundException("Recipe not found with id: " + recipeWithPortion.getRecipeId()));

				UserSpecificRecipe userSpecificRecipe = new UserSpecificRecipe();
				userSpecificRecipe.setDay(day);
				userSpecificRecipe.setRecipe(recipe);
				userSpecificRecipe.setPortions(recipeWithPortion.getPortions());
				userSpecificRecipeRepository.save(userSpecificRecipe);

				day.getUserSpecificRecipes().add(userSpecificRecipe);
			}
		}

		return dayRepository.save(day);
	}
	
	public List<UserSpecificRecipeDTO> getUserSpecificRecipesForDay(Long dayId) {
		Day day = dayRepository.findById(dayId)
				.orElseThrow(() -> new EntityNotFoundException("Day not found with id: " + dayId));
		return day.getUserSpecificRecipes().stream()
				.map(UserSpecificRecipeDTO::new)
				.collect(Collectors.toList());
	}


	//////////////////////////////////
	//            Helper            //
	//////////////////////////////////

	private static void chechIfDateAndWeekIdAreNotNull(DayRequest dayRequest) {
		if (dayRequest.getDate() == null) {
			throw new IllegalArgumentException("Date must not be null.");
		}
		if (dayRequest.getWeekId() == null) {
			throw new IllegalArgumentException("WeekId must not be null.");
		}
	}

	private void checkIfRecipesWithPortionsHaveCorrectValues(List<DayRequest.RecipeWithPortion> recipeRequests) {
		for (DayRequest.RecipeWithPortion recipeWithPortion : recipeRequests) {
			if (recipeWithPortion.getRecipeId() == null || recipeWithPortion.getPortions() <= 0) {
				throw new IllegalArgumentException("Invalid recipeId or portions in the request.");
			}

			if (!recipeRepository.existsById(recipeWithPortion.getRecipeId())) {
				throw new EntityNotFoundException(
						"Recipe not found with id: " + recipeWithPortion.getRecipeId()
				);
			}
		}
	}
}
