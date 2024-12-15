package de.team5.sopra.backend.controller;

import de.team5.sopra.backend.models.User;
import de.team5.sopra.backend.service.JwtService;
import de.team5.sopra.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("There was no user found with the given ID!");
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected internal error occurred!");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            userService.registerUser(user.getUsername(), user.getPassword());
            return ResponseEntity.ok("User successfully registered!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        try {
            // Debug-Log
            System.out.println("Login attempt for user: " + loginRequest.getUsername());

            // Authenticate
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Get user after successful authentication
            User user = userService.getUserByUsername(loginRequest.getUsername());

            // Generate token
            String token = jwtService.generateToken(user);

            // Debug-Log
            System.out.println("Token generated: " + token);

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", user.getId());
            response.put("message", "Login erfolgreich!");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Debug-Log
            System.out.println("Login failed: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Login fehlgeschlagen: " + e.getMessage());
            return ResponseEntity.status(401).body(errorResponse);
        }
    }
}

