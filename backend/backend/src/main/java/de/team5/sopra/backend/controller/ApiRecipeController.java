package de.team5.sopra.backend.controller;

import de.team5.sopra.backend.service.ApiRecipeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiRecipeController {
    private final ApiRecipeService apiRecipeService;

    public ApiRecipeController(ApiRecipeService apiRecipeService) {
        this.apiRecipeService = apiRecipeService;
    }

    @GetMapping("/search-recipes")
    public String searchRecipes(@RequestParam String query) {
        try{
            return apiRecipeService.searchRecipes(query);
        } catch (Exception e) {
            return "Error while retrieving the recipes" + e.getMessage();
        }
    }
}

