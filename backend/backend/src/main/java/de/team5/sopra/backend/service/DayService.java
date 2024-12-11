package de.team5.sopra.backend.service;

import java.util.List;
import java.util.Optional;

import de.team5.sopra.backend.models.DayRequest;
import de.team5.sopra.backend.models.Week;
import de.team5.sopra.backend.repository.RecipeRepository;
import de.team5.sopra.backend.repository.WeekRepository;
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
        dayRepository.deleteById(id);
    }

    public Day addRecipeToDay(Long id, Recipe recipe){
        Optional<Day> dayOptionalObject = dayRepository.findById(id);
        if(dayOptionalObject.isPresent()){
            Day day = dayOptionalObject.get();
            List<Recipe> dayRecipeList = day.getRecipes();
            dayRecipeList.add(recipe);
            return dayRepository.save(day);
        } else{
            throw new EntityNotFoundException("No day with the ID: "+id+" exists.");
        }
    }

    public void removeRecipeFromDay(Long dayId, Long recipeId) {
        Day day = dayRepository.findById(dayId)
                .orElseThrow(() -> new EntityNotFoundException("Day not found with id: " + dayId));

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with id: " + recipeId));

        if (day.getRecipes().contains(recipe)) {
            day.getRecipes().remove(recipe); // Rezept aus der Liste entfernen
            dayRepository.save(day);        // Änderungen speichern
        } else {
            throw new IllegalArgumentException("Recipe is not assigned to the specified day.");
        }
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
}
