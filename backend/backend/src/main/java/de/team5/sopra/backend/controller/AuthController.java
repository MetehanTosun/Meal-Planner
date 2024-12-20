package de.team5.sopra.backend.controller;

import de.team5.sopra.backend.models.User;
import de.team5.sopra.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User registered = userService.registerUser(user.getUsername(), user.getPassword());
            return ResponseEntity.ok(Map.of(
                    "userId", registered.getId(),
                    "message", "Registration successful"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        try {
            User user = userService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
            if (user != null) {
                return ResponseEntity.ok(Map.of(
                        "userId", user.getId(),
                        "message", "Login successful"
                ));
            } else {
                return ResponseEntity.badRequest().body("Invalid credentials");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}