package de.team5.sopra.backend.controller.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.team5.sopra.backend.controller.RecipeController;
import de.team5.sopra.backend.models.Ingredient;
import de.team5.sopra.backend.models.Recipe;
import de.team5.sopra.backend.models.enums.FoodType;
import de.team5.sopra.backend.service.RecipeService;
import de.team5.sopra.backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Umfasst Tests für den RecipeController.
 * Wir verwenden @WebMvcTest, um den Controller isoliert zu testen,
 * und mocken den RecipeService mittels @MockBean.
 */
@WebMvcTest(controllers = RecipeController.class)
@Import(UserService.class)
class RecipeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RecipeService recipeService;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@Nested
	@DisplayName("GET /recipes")
	class GetAllRecipesTests {


		@Test
		@DisplayName("soll 200 OK zurückgeben und eine Liste von Recipes liefern")
		void testGetAllRecipes() throws Exception {
			Recipe r1 = new Recipe();
			r1.setId(1L);
			r1.setName("Tomato Soup");
			r1.setFoodType(FoodType.VEGAN);
			r1.setTime(20);

			when(recipeService.getAllRecipes())
					.thenReturn(List.of(r1));

			mockMvc.perform(get("/recipes"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].id").value(1L))
					.andExpect(jsonPath("$[0].name").value("Tomato Soup"))
					.andExpect(jsonPath("$[0].foodtype").value("VEGAN"));
		}

		@Test
		@DisplayName("soll leere Liste zurückgeben, wenn keine Recipes vorhanden sind")
		void testGetAllRecipesEmpty() throws Exception {
			when(recipeService.getAllRecipes())
					.thenReturn(new ArrayList<>());

			mockMvc.perform(get("/recipes"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$").isArray())
					.andExpect(jsonPath("$.length()").value(0));
		}
	}

	@Nested
	@DisplayName("GET /recipes/{id}")
	class GetRecipeByIdTests {

		@Test
		@DisplayName("soll 200 OK zurückgeben und das gesuchte Recipe liefern")
		void testGetRecipeByIdSuccess() throws Exception {
			Recipe r = new Recipe();
			r.setId(2L);
			r.setName("Pasta Bolognese");
			r.setFoodType(FoodType.MEAT);
			r.setTime(30);

			when(recipeService.getRecipeById(2L)).thenReturn(r);

			System.out.println("Returned Data:" +objectMapper.writeValueAsString(recipeService.getRecipeById(2L)));
			mockMvc.perform(get("/recipes/2"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(2L))
					.andExpect(jsonPath("$.name").value("Pasta Bolognese"))
					.andExpect(jsonPath("$.foodType").value("MEAT"));
		}

		@Test
		@DisplayName("soll 404 NOT_FOUND zurückgeben, wenn Recipe nicht existiert")
		void testGetRecipeByIdNotFound() throws Exception {
			when(recipeService.getRecipeById(999L))
					.thenThrow(new org.springframework.web.server.ResponseStatusException(
							org.springframework.http.HttpStatus.NOT_FOUND,
							"Recipe with the ID: 999 wasn't found."
					));

			mockMvc.perform(get("/recipes/999"))
					.andExpect(status().isNotFound())
					.andExpect(status().reason("Recipe with the ID: 999 wasn't found."));
		}
	}

	@Nested
	@DisplayName("POST /recipes")
	class CreateRecipeTests {

		@Test
		@DisplayName("soll 201 CREATED zurückgeben bei erfolgreichem Anlegen eines Recipes")
		void testCreateRecipeSuccess() throws Exception {
			Recipe requestRecipe = new Recipe();
			requestRecipe.setName("Tomato Soup");
			requestRecipe.setFoodType(FoodType.VEGAN);
			requestRecipe.setTime(20);
			requestRecipe.setInstructions(List.of("Chop tomatoes", "Cook with garlic", "Blend"));

			Ingredient ingredient1 = new Ingredient();
			ingredient1.setName("Tomatoes");
			ingredient1.setAmount(300.0);

			requestRecipe.setIngredients(List.of(ingredient1));

			Recipe savedRecipe = new Recipe();
			savedRecipe.setId(10L);
			savedRecipe.setName("Tomato Soup");
			savedRecipe.setFoodType(FoodType.VEGAN);
			savedRecipe.setTime(20);
			savedRecipe.setInstructions(List.of("Chop tomatoes", "Cook with garlic", "Blend"));
			savedRecipe.setIngredients(List.of(ingredient1));

			when(recipeService.createRecipe(ArgumentMatchers.any(Recipe.class)))
					.thenReturn(savedRecipe);

			mockMvc.perform(post("/recipes")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(requestRecipe)))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.id").value(10L))
					.andExpect(jsonPath("$.name").value("Tomato Soup"))
					.andExpect(jsonPath("$.ingredients[0].name").value("Tomatoes"));
		}
	}

	@Nested
	@DisplayName("PUT /recipes/{id}")
	class UpdateRecipeTests {

		@Test
		@DisplayName("soll 200 OK zurückgeben und das aktualisierte Recipe liefern")
		void testUpdateRecipeSuccess() throws Exception {
			Recipe requestBody = new Recipe();
			requestBody.setName("Vegan Pasta");
			requestBody.setFoodType(FoodType.VEGAN);
			requestBody.setTime(25);
			requestBody.setInstructions(List.of("Boil water", "Cook pasta"));
			List<Ingredient> newIngredients = new ArrayList<>();
			Ingredient ingr = new Ingredient();
			ingr.setName("Pasta");
			ingr.setAmount(250.0);
			newIngredients.add(ingr);
			requestBody.setIngredients(newIngredients);

			Recipe updatedRecipe = new Recipe();
			updatedRecipe.setId(5L);
			updatedRecipe.setName("Vegan Pasta");
			updatedRecipe.setFoodType(FoodType.VEGAN);
			updatedRecipe.setTime(25);
			updatedRecipe.setInstructions(List.of("Boil water", "Cook pasta"));
			updatedRecipe.setIngredients(newIngredients);

			when(recipeService.updateRecipe(eq(5L), ArgumentMatchers.any(Recipe.class)))
					.thenReturn(updatedRecipe);

			mockMvc.perform(put("/recipes/5")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(requestBody)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(5L))
					.andExpect(jsonPath("$.name").value("Vegan Pasta"))
					.andExpect(jsonPath("$.ingredients[0].name").value("Pasta"));
		}

		@Test
		@DisplayName("soll 404 NOT_FOUND zurückgeben, wenn das Recipe nicht existiert")
		void testUpdateRecipeNotFound() throws Exception {
			when(recipeService.updateRecipe(eq(999L), any()))
					.thenThrow(new org.springframework.web.server.ResponseStatusException(
							org.springframework.http.HttpStatus.NOT_FOUND,
							"Recipe with ID 999 not found"
					));

			Recipe requestBody = new Recipe();
			requestBody.setName("Non-existent");

			mockMvc.perform(put("/recipes/999")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(requestBody)))
					.andExpect(status().isNotFound())
					.andExpect(status().reason("Recipe with ID 999 not found"));
		}
	}

	@Nested
	@DisplayName("DELETE /recipes/{id}")
	class DeleteRecipeTests {

		@Test
		@DisplayName("soll 404 NOT_FOUND zurückgeben, wenn das Recipe nicht existiert")
		@WithMockUser(username = "testUser", roles = {"USER"})
		void testDeleteRecipeNotFound() throws Exception {
			doThrow(new org.springframework.web.server.ResponseStatusException(
					org.springframework.http.HttpStatus.NOT_FOUND, "Recipe not found!"
			)).when(recipeService).deleteRecipeById(999L);

			mockMvc.perform(delete("/recipes/999"))
					.andExpect(status().isNotFound())
					.andExpect(status().reason("Recipe not found"));
		}
	}

	@Nested
	@DisplayName("GET /recipes/{id}/ingredients")
	class GetAllIngredientsTests {

		@Test
		@DisplayName("soll 200 OK und eine Liste von Ingredients zurückgeben")
		void testGetAllIngredients() throws Exception {
			Ingredient ingr1 = new Ingredient();
			ingr1.setName("Tomatoes");
			ingr1.setAmount(300.0);

			Ingredient ingr2 = new Ingredient();
			ingr2.setName("Basil");
			ingr2.setAmount(5.0);

			when(recipeService.getAllIngredientsForRecipe(3L))
					.thenReturn(List.of(ingr1, ingr2));

			mockMvc.perform(get("/recipes/3/ingredients"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].name").value("Tomatoes"))
					.andExpect(jsonPath("$[1].name").value("Basil"));
		}

		@Test
		@DisplayName("soll 404 NOT_FOUND zurückgeben, wenn das Recipe nicht existiert")
		void testGetAllIngredientsRecipeNotFound() throws Exception {
			when(recipeService.getAllIngredientsForRecipe(999L))
					.thenThrow(new org.springframework.web.server.ResponseStatusException(
							org.springframework.http.HttpStatus.NOT_FOUND, "Recipe not found"
					));

			mockMvc.perform(get("/recipes/999/ingredients"))
					.andExpect(status().isNotFound())
					.andExpect(status().reason("Recipe not found"));
		}
	}

	@Nested
	@DisplayName("DELETE /recipes/{recipeId}/ingredients/{ingredientName}")
	class DeleteIngredientTests {

		@Test
		@DisplayName("soll 204 NO_CONTENT zurückgeben, wenn Ingredient erfolgreich gelöscht wurde")
		void testDeleteIngredientSuccess() throws Exception {
			doNothing().when(recipeService).deleteIngredient(1L, "Tomatoes");

			mockMvc.perform(delete("/recipes/1/ingredients/Tomatoes"))
					.andExpect(status().isNoContent());
		}

		@Test
		@DisplayName("soll 404 NOT_FOUND zurückgeben, wenn Ingredient nicht existiert")
		void testDeleteIngredientNotFound() throws Exception {
			doThrow(new org.springframework.web.server.ResponseStatusException(
					org.springframework.http.HttpStatus.NOT_FOUND,
					"The ingredient Cheese was not found in recipe 1"
			)).when(recipeService).deleteIngredient(1L, "Cheese");

			mockMvc.perform(delete("/recipes/1/ingredients/Cheese"))
					.andExpect(status().isNotFound())
					.andExpect(status().reason("The ingredient Cheese was not found in recipe 1"));
		}
	}

	@Nested
	@DisplayName("GET /recipes/{id}/instructions")
	class GetInstructionsTests {

		@Test
		@DisplayName("soll 200 OK und eine Liste von Instructions zurückgeben")
		void testGetInstructionsSuccess() throws Exception {
			List<String> instructions = List.of("Chop veggies", "Boil water", "Serve hot");

			when(recipeService.getInstructions(7L))
					.thenReturn(instructions);

			mockMvc.perform(get("/recipes/7/instructions"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[0]").value("Chop veggies"))
					.andExpect(jsonPath("$[1]").value("Boil water"))
					.andExpect(jsonPath("$[2]").value("Serve hot"));
		}

		@Test
		@DisplayName("soll 404 NOT_FOUND zurückgeben, wenn das Recipe nicht existiert")
		void testGetInstructionsRecipeNotFound() throws Exception {
			when(recipeService.getInstructions(777L))
					.thenThrow(new org.springframework.web.server.ResponseStatusException(
							org.springframework.http.HttpStatus.NOT_FOUND,
							"Recipe with ID 777 not found"
					));

			mockMvc.perform(get("/recipes/777/instructions"))
					.andExpect(status().isNotFound())
					.andExpect(status().reason("Recipe with ID 777 not found"));
		}
	}
}
