package de.team5.sopra.backend.controller;

import de.team5.sopra.backend.dto.WeekRequest;
import de.team5.sopra.backend.models.Week;
import de.team5.sopra.backend.service.WeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/weeks")
public class WeekController {

	@Autowired
	private WeekService weekService;

	@GetMapping
	public ResponseEntity<List<Week>> getAllWeeks() {
		return ResponseEntity.ok().body(weekService.getAllWeeks());
	}

	@PostMapping
	public ResponseEntity<Week> createWeek(@RequestBody WeekRequest week) {
		try{
			Week createdWeek = weekService.createWeek(week);
			return ResponseEntity.ok().body(createdWeek);
		}catch(IllegalArgumentException e){
			return ResponseEntity.badRequest().body(null);
		}catch(Exception e){
			System.out.println(e);
			return ResponseEntity.internalServerError().body(null);
		}
	}

}
