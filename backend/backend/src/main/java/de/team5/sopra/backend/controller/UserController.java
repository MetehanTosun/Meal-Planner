package de.team5.sopra.backend.controller;

import de.team5.sopra.backend.dto.UserStatisticsDTO;
import de.team5.sopra.backend.exception.UserNotFoundException;
import de.team5.sopra.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserController {

	private final UserService userService;

	@GetMapping("/users/{userId}/statistics")
	public ResponseEntity<?> getUserStatistics(@PathVariable Long userId) {
		try {
			UserStatisticsDTO statistics = userService.calculateUserStatistics(userId);
			return ResponseEntity.ok(statistics);
		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
		}
	}
}
