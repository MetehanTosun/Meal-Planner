package de.team5.sopra.backend.controller;

import de.team5.sopra.backend.dto.WeekRequest;
import de.team5.sopra.backend.dto.WeeklyCreationDTO;
import de.team5.sopra.backend.models.User;
import de.team5.sopra.backend.models.Week;
import de.team5.sopra.backend.service.UserService;
import de.team5.sopra.backend.service.WeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/weeks")
public class WeekController {

	@Autowired
	private WeekService weekService;
	@Autowired
	private UserService userService;
	@GetMapping
	public ResponseEntity<List<Week>> getAllWeeks() {
		List<Week> weeks = weekService.getAllWeeks();
		return ResponseEntity.ok(weeks);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Week> getWeek(@PathVariable Long id) {
		return weekService.getWeekById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/current/{userId}")
	public ResponseEntity<?> getCurrentWeek(@PathVariable Long userId) {
		try {
			System.out.println("Fetching current week for user: " + userId);
			Week currentWeek = weekService.getOrCreateCurrentWeek(userId);
			return ResponseEntity.ok(currentWeek);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			System.err.println("Error getting current week: " + e.getMessage());
			return ResponseEntity.internalServerError().body("Error fetching current week");
		}
	}

	/**
	 * Create a new Week. User ID is mandatory, or it throws an exception.
	 */
	@PostMapping
	public ResponseEntity<Week> createWeek(@RequestBody WeekRequest weekRequest) {
		try {
			Week created = weekService.createWeek(weekRequest);
			return ResponseEntity.ok(created);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteWeek(@PathVariable Long id) {
		try {
			weekService.deleteWeek(id);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/create/user/{id}")
	public ResponseEntity<?> createDefaultNewWeek(@PathVariable Long id, @RequestBody WeeklyCreationDTO weeklyCreationDTO) {
		System.out.println("Entry Data createDefaultNewWeek: " + id + " and startDate: " + weeklyCreationDTO.getStartDate());

		try{
			Week createdWeek = weekService.createNewWeekForUser(id, weeklyCreationDTO.getStartDate());
			System.out.println("Created Week: " + createdWeek);
			return ResponseEntity.ok(createdWeek);
		}catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}

	}

	@GetMapping("/range/{userId}")
	public ResponseEntity<List<Week>> getWeeksInRange(@PathVariable Long userId, @RequestParam int count) {
		try {
			List<Week> weeks = weekService.getWeeksInRange(userId, count);
			return ResponseEntity.ok(weeks);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	/**
	 *
	 * @param userId
	 * @return
	 */
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Week>> getWeeksForUser(@PathVariable Long userId) {
		User user = userService.getUserById(userId);
		List<Week> weeks = weekService.getWeeksByUser(user);
		return ResponseEntity.ok(weeks);
	}

}
