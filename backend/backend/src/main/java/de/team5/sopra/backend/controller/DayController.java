package de.team5.sopra.backend.controller;

//import java.lang.foreign.Linker.Option;
import java.util.List;

import de.team5.sopra.backend.dto.AddRecipeToDayRequest;
import de.team5.sopra.backend.dto.DayRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.team5.sopra.backend.models.Day;
import de.team5.sopra.backend.service.DayService;
import org.springframework.web.server.ResponseStatusException;

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
        List<Day> days = dayService.getAllDays();
        days.forEach(System.out::println);
        return days;
    }

    @GetMapping("/{id}")
    public Day getDayById(@PathVariable("id") Long id){
        return dayService.getDayById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDay(@PathVariable("id") Long id){
        try{
            dayService.deleteDay(id);
            return ResponseEntity.noContent().build();
        }catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

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
    @PutMapping("/{id}")
    public ResponseEntity<Day> updateDay(@PathVariable Long id, @RequestBody DayRequest dayRequest) {
        try {
            Day updatedDay = dayService.updateDay(id, dayRequest);
            return ResponseEntity.ok(updatedDay);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // NEW FEATURE

    @PostMapping("/{dayId}/add-recipe")
    public ResponseEntity<Day> addRecipeToDayWithPortions(
            @PathVariable Long dayId,
            @RequestBody @Valid AddRecipeToDayRequest request) {
        try {
            Day updatedDay = dayService.addRecipeToDayWithPortions(dayId, request.getRecipeId(), request.getPortions());
            return ResponseEntity.ok(updatedDay);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
