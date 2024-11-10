package de.team5.sopra.backend.service;

import java.util.List;
import java.util.Optional;
import org.hibernate.action.internal.OrphanRemovalAction;
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

    /*
     * This Operation retrieves every single day from the table.
     */
    public List<Day> getAllDays(){
        return dayRepository.findAll();
    }

    public Optional<Day> getDayById(Long id){
        Optional<Day> searchedDay = dayRepository.findById(id);
        if(!searchedDay.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The day with the ID: "+id+" wasn't found.");
        }
        return searchedDay;
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
}
