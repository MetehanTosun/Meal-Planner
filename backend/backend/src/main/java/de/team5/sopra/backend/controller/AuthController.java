package de.team5.sopra.backend.controller;

import de.team5.sopra.backend.models.User;
import de.team5.sopra.backend.repository.UserRepository;
import de.team5.sopra.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final View error;

    public AuthController(UserService userService, UserRepository userRepository, View error) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.error = error;
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try{
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        }catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("There was no user found with the given ID!");
        }catch(Exception e) {
            return ResponseEntity.internalServerError().body("A unexpected internal error occoured!");
        }

    }
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            userService.registerUser(user.getUsername(), user.getPassword());
            return ResponseEntity.ok("Benutzer erfolgreich registriert!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody User user) {
        User responseUser = userService.authenticateUser(user.getUsername(), user.getPassword());
        if (responseUser != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("userId", responseUser.getId());
            response.put("message", "Login erfolgreich!");
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Fehler beim Login!");
            return ResponseEntity.status(401).body(errorResponse);
        }
    }
}

