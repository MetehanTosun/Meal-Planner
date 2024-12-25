package de.team5.sopra.backend.controller;

//import java.lang.foreign.Linker.Option;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.team5.sopra.backend.dto.AddRecipeToDayRequest;
import de.team5.sopra.backend.dto.DayDTO;
import de.team5.sopra.backend.dto.DayRequest;
import de.team5.sopra.backend.dto.RecipeDTO;
import de.team5.sopra.backend.models.Recipe;
import de.team5.sopra.backend.models.User;
import de.team5.sopra.backend.models.Week;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
        return dayService.getAllDays();
    }

    @GetMapping("/{id}")
    public Day getDayById(@PathVariable("id") Long id){
        Day day = dayService.getDayById(id);
        return day;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delteDay(@PathVariable("id") Long id){
        try{
            dayService.deleteDay(id);
            return ResponseEntity.noContent().build();
        }catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
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

    @PostMapping("/{dayId}/add-recipe")
    public ResponseEntity<DayDTO> addRecipeToDay(
            @PathVariable Long dayId,
            @RequestBody RecipeDTO recipeDTO
    ) {
        try {
            DayDTO updatedDay = dayService.addRecipeToDay(dayId, recipeDTO);
            return ResponseEntity.ok(updatedDay);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/byName/{dayName}")
    public ResponseEntity<DayDTO> getDayByName(@PathVariable String dayName) {
        try {
            // Wir holen die aktuelle Woche des eingeloggten Users
            User currentUser = getCurrentUser();
            Week currentWeek = currentUser.getWeeks().get(currentUser.getWeeks().size() - 1); // letzte/aktuelle Woche

            // Suchen den passenden Tag
            Day foundDay = null;
            List<Day> days = currentWeek.getDays();

            for (Day day : days) {
                if (getDayName(day.getDate()).equalsIgnoreCase(dayName)) {
                    foundDay = day;
                    break;
                }
            }

            if (foundDay == null) {
                throw new EntityNotFoundException("Day not found: " + dayName);
            }

            return ResponseEntity.ok(DayDTO.fromDay(foundDay));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // Hilfsmethode um aus einem Date den Wochentag zu bekommen
    private String getDayName(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);
    }

    @DeleteMapping("/{dayId}/remove-recipe/{recipeId}")
    public ResponseEntity<DayDTO> removeRecipeFromDay(@PathVariable Long dayId, @PathVariable Long recipeId) {
        try {
            DayDTO updatedDay = dayService.removeRecipeFromDay(dayId, recipeId);
            return ResponseEntity.ok(updatedDay);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // TODO: ADD THIS POST-Mapping
//    @PostMapping("/days/add-recipe")
//    public ResponseEntity<Day> addRecipeToDayWithIdAndPortion(@RequestBody @Valid AddRecipeToDayRequest request) {
//        try {
//            Day updatedDay = dayService.addRecipeToDayWithIdAndPortion(request);
//            return ResponseEntity.ok(updatedDay);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(null);
//        }
//    }


}
