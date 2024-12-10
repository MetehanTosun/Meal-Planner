package de.team5.sopra.backend.controller;

import de.team5.sopra.backend.dto.ApiRecipeDTO;
import de.team5.sopra.backend.service.ApiRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class ApiRecipeController {
    private final ApiRecipeService apiRecipeService;

    public ApiRecipeController(ApiRecipeService apiRecipeService) {
        this.apiRecipeService = apiRecipeService;
    }

    @GetMapping("/search-recipes")
    public ResponseEntity<?> searchRecipes(@RequestParam String query, @RequestParam(required = false) String diet) {
        try {
            List<ApiRecipeDTO> recipes = apiRecipeService.searchRecipes(query, diet);
            return ResponseEntity.ok(recipes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Error while retrieving the recipes: " + e.getMessage());
        }
    }
}