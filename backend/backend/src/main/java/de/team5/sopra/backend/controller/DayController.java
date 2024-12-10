package de.team5.sopra.backend.controller;

//import java.lang.foreign.Linker.Option;
import java.util.List;
import java.util.Optional;

import de.team5.sopra.backend.models.DayRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.team5.sopra.backend.dto.DayDto;
import de.team5.sopra.backend.models.Day;
import de.team5.sopra.backend.service.DayService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/days")
public class DayController {
    
    @Autowired
    private DayService dayService;

    /*
     * Never used in Frontend
     * TODO: Implement tests with this
     */
    @GetMapping
    public List<Day> getAllDays(){
        return dayService.getAllDays();
    }

    @GetMapping("/{id}")
    public Day getDayById(@PathVariable("id") Long id){
        Day day = dayService.getDayById(id);
        return day;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delteDay(@PathVariable("id") Long id){
        dayService.deleteDay(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{dayId}/recipes/{recipeId}")
    public ResponseEntity<String> removeRecipeFromDay(
            @PathVariable Long dayId,
            @PathVariable Long recipeId) {
        try {
            dayService.removeRecipeFromDay(dayId, recipeId);
            return ResponseEntity.ok("Recipe successfully removed from the day.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }
    /*
     * String in the JSON send is okay, but the name has to be Uppercase because of ENUM in Day
     */
    @PostMapping
    public ResponseEntity<Day> createDay(@RequestBody DayRequest dayRequest) {
        try {
            Day createdDay = dayService.createDay(dayRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDay);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
