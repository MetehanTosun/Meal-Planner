package de.team5.sopra.backend.controller.general;

import de.team5.sopra.backend.models.Recipe;
import de.team5.sopra.backend.service.general.ShoppingListService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shopping-list")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    public ShoppingListController(ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
    }

    @PostMapping
    public Map<String, Double> generateShoppingList(@RequestBody List<Recipe> recipes) {
        return shoppingListService.generateShoppingList(recipes);
    }
}