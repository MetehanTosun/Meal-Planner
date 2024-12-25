package de.team5.sopra.backend.service;

import java.util.*;

import de.team5.sopra.backend.dto.AddRecipeToDayRequest;
import de.team5.sopra.backend.dto.DayDTO;
import de.team5.sopra.backend.dto.DayRequest;
import de.team5.sopra.backend.dto.RecipeDTO;
import de.team5.sopra.backend.models.UserSpecificRecipe;
import de.team5.sopra.backend.models.Week;
import de.team5.sopra.backend.repository.RecipeRepository;
import de.team5.sopra.backend.repository.WeekRepository;
import de.team5.sopra.backend.repository.general.UserSpecificRecipeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import de.team5.sopra.backend.models.Day;
import de.team5.sopra.backend.models.Recipe;
import de.team5.sopra.backend.repository.DayRepository;
import jakarta.persistence.EntityNotFoundException;

/*
 * Buisness Logic Layer
 */
@Service
public class DayService {
    
    @Autowired
    private DayRepository dayRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private WeekRepository weekRepository;

    private UserSpecificRecipeRepository userSpecificRecipeRepository;

    /*
     * This Operation retrieves every single day from the table.
     */
    public List<Day> getAllDays(){
        return dayRepository.findAll();
    }

    public Day getDayById(Long id){
        Optional<Day> searchedDay = dayRepository.findById(id);
        if(searchedDay.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The day with the ID: "+id+" wasn't found.");
        }
        return searchedDay.get();
    }

    public Day saveDay(Day day){
        return dayRepository.save(day);
    }

    public void deleteDay(Long id){
        if(recipeRepository.existsById(id)){
            dayRepository.deleteById(id);
        }else{
            throw new EntityNotFoundException("Day not found with id: "+id);
        }
    }

    @Transactional
    public DayDTO addRecipeToDay(Long dayId, RecipeDTO recipeDTO) {
        Day day = dayRepository.findById(dayId)
                .orElseThrow(() -> new EntityNotFoundException("No day with the ID: " + dayId + " exists."));

        Recipe recipe;
        if (recipeDTO.getId() != null) {
            recipe = recipeRepository.findById(recipeDTO.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Recipe not found: " + recipeDTO.getId()));
        } else {
            recipe = new Recipe();
            recipe.setName(recipeDTO.getName());
            recipe.setTime(recipeDTO.getTime());
            recipe.setFoodtype(recipeDTO.getFoodtype());
            // Weitere Felder setzen
            recipe = recipeRepository.save(recipe);
        }

        if (!day.getRecipes().contains(recipe)) {
            day.getRecipes().add(recipe);
        }

        Day savedDay = dayRepository.save(day);
        return DayDTO.fromDay(savedDay);
    }


    @Transactional
    public DayDTO removeRecipeFromDay(Long dayId, Long recipeId) {
        Day day = dayRepository.findById(dayId)
                .orElseThrow(() -> new EntityNotFoundException("Day not found: " + dayId));

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found: " + recipeId));

        day.getRecipes().remove(recipe);
        Day updatedDay = dayRepository.save(day);

        return DayDTO.fromDay(updatedDay);
    }

    public Day createDay(DayRequest dayRequest) {
        Day day = new Day();
        day.setDate(dayRequest.getDate());

        // Verknüpfe den Tag mit einer Woche, falls angegeben
        if (dayRequest.getWeekId() != null) {
            Week week = weekRepository.findById(dayRequest.getWeekId())
                    .orElseThrow(() -> new EntityNotFoundException("Week not found with id: " + dayRequest.getWeekId()));
            day.setWeek(week);
        }

        // Füge Rezepte hinzu, falls angegeben
        if (dayRequest.getRecipeIds() != null && !dayRequest.getRecipeIds().isEmpty()) {
            List<Recipe> recipes = recipeRepository.findAllById(dayRequest.getRecipeIds());
            day.setRecipes(recipes);
        }


        return dayRepository.save(day);
    }
    public Day updateDay(Long id, DayRequest dayRequest) {
        Optional<Day> dayOptional = dayRepository.findById(id);
        if (dayOptional.isEmpty()) {
            throw new EntityNotFoundException("Day not found with id: " + id);
        }
        Day day = dayOptional.get();
        if (dayRequest.getDate() != null) {
            day.setDate(dayRequest.getDate());
        }
        if (dayRequest.getWeekId() != null) {
            Week week = weekRepository.findById(dayRequest.getWeekId())
                    .orElseThrow(() -> new EntityNotFoundException("Week not found with id: " + dayRequest.getWeekId()));
            day.setWeek(week);
        }
        if (dayRequest.getRecipeIds() != null && !dayRequest.getRecipeIds().isEmpty()) {
            List<Recipe> recipes = recipeRepository.findAllById(dayRequest.getRecipeIds());
            day.setRecipes(recipes);
        }
        return dayRepository.save(day);
    }
    //TODO: ADD THIS FUNCTION
//    public Day addRecipeToDayWithIdAndPortion(AddRecipeToDayRequest request) {
//        // Retrieve the Day
//        Day day = dayRepository.findById(request.getDayId())
//                .orElseThrow(() -> new IllegalArgumentException("Day not found with ID: " + request.getDayId()));
//
//        // Retrieve the Recipe
//        Recipe recipe = recipeRepository.findById(request.getRecipeId())
//                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with ID: " + request.getRecipeId()));
//
//        // Create a UserSpecificRecipe
//        UserSpecificRecipe userSpecificRecipe = new UserSpecificRecipe();
//        userSpecificRecipe.setRecipe(recipe);
//        userSpecificRecipe.setDay(day);
//        if (request.getPortions() != null) {
//            userSpecificRecipe.setPortions(request.getPortions());
//        }
//
//        // Save the UserSpecificRecipe
//        userSpecificRecipeRepository.save(userSpecificRecipe);
//
//        // Add the recipe to the Day
//        day.getRecipes().add(userSpecificRecipe);
//        return dayRepository.save(day);
//    }
}
