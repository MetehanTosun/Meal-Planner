package de.team5.sopra.backend.controller;

//import java.lang.foreign.Linker.Option;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.team5.sopra.backend.dto.DayDto;
import de.team5.sopra.backend.models.Day;
import de.team5.sopra.backend.models.Day.Weekday;
import de.team5.sopra.backend.repository.DayRepository;
import de.team5.sopra.backend.service.DayService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;

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
    public ResponseEntity<Day> getDayById(@PathVariable("id") Long id){
        Optional<Day> day = dayService.getDayById(id);
        if(!day.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Day with ID %s not found!", id));
        }
        return ResponseEntity.ok(day.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delteDay(@PathVariable("id") Long id){
        dayService.deleteDay(id);
        return ResponseEntity.noContent().build();
    }
    /*
     * String in the JSON send is okay, but the name has to be Uppercase because of ENUM in Day
     */
    @PostMapping
    public ResponseEntity<Day> createDay(@Valid @RequestBody DayDto requestBody){
        Weekday weekday;
        try {
            weekday = Weekday.valueOf(requestBody.getName().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid day name: " +requestBody.getName());
        }
        Day day = new Day(weekday, requestBody.getRecipes());
        dayService.saveDay(day);

        return ResponseEntity.status(HttpStatus.CREATED).body(day);
    }
}
