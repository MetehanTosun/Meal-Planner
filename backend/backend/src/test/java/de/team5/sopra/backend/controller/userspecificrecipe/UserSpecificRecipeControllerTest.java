package de.team5.sopra.backend.controller.userspecificrecipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.team5.sopra.backend.controller.general.UserSpecificRecipeController;
import de.team5.sopra.backend.models.UserSpecificRecipe;
import de.team5.sopra.backend.service.general.UserSpecificRecipeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserSpecificRecipeController.class)
class UserSpecificRecipeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserSpecificRecipeService userSpecificRecipeService;

	@Autowired
	private ObjectMapper objectMapper;

	@Nested
	@DisplayName("GET /user-specific-recipes")
	class GetAllUserSpecificRecipesTests {

		@Test
		@DisplayName("should return 200 OK and a list of UserSpecificRecipes")
		void testGetAllUserSpecificRecipes() throws Exception {
			UserSpecificRecipe usr1 = new UserSpecificRecipe();
			usr1.setId(1L);
			usr1.setPortions(2);

			when(userSpecificRecipeService.getAllUserSpecificRecipes())
					.thenReturn(List.of(usr1));

			mockMvc.perform(get("/user-specific-recipes"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].id").value(1L))
					.andExpect(jsonPath("$[0].portions").value(2));
		}

		@Test
		@DisplayName("should return an empty list if none found")
		void testGetAllEmpty() throws Exception {
			when(userSpecificRecipeService.getAllUserSpecificRecipes())
					.thenReturn(new ArrayList<>());

			mockMvc.perform(get("/user-specific-recipes"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.length()").value(0));
		}
	}

	@Nested
	@DisplayName("GET /user-specific-recipes/{id}")
	class GetByIdTests {

		@Test
		@DisplayName("should return 200 OK and the specified UserSpecificRecipe")
		void testGetById() throws Exception {
			UserSpecificRecipe usr = new UserSpecificRecipe();
			usr.setId(10L);
			usr.setPortions(4);

			when(userSpecificRecipeService.getUserSpecificRecipeById(10L))
					.thenReturn(usr);

			mockMvc.perform(get("/user-specific-recipes/10"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(10L))
					.andExpect(jsonPath("$.portions").value(4));
		}

		@Test
		@DisplayName("should return 404 NOT_FOUND if not exist")
		void testGetByIdNotFound() throws Exception {
			when(userSpecificRecipeService.getUserSpecificRecipeById(999L))
					.thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND,
							"UserSpecificRecipe with ID 999 not found"));

			mockMvc.perform(get("/user-specific-recipes/999"))
					.andExpect(status().isNotFound())
					.andExpect(status().reason("UserSpecificRecipe with ID 999 not found"));
		}
	}

	@Nested
	@DisplayName("POST /user-specific-recipes")
	class CreateTests {

		@Test
		@DisplayName("should return 201 CREATED for valid input")
		void testCreateSuccess() throws Exception {
			UserSpecificRecipe requestBody = new UserSpecificRecipe();
			requestBody.setPortions(3);

			UserSpecificRecipe created = new UserSpecificRecipe();
			created.setId(11L);
			created.setPortions(3);

			when(userSpecificRecipeService.createUserSpecificRecipe(ArgumentMatchers.any(UserSpecificRecipe.class)))
					.thenReturn(created);

			mockMvc.perform(post("/user-specific-recipes")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(requestBody)))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.id").value(11L))
					.andExpect(jsonPath("$.portions").value(3));
		}

		@Test
		@DisplayName("should return 400 BAD_REQUEST if portions <= 0")
		void testCreateBadRequest() throws Exception {
			when(userSpecificRecipeService.createUserSpecificRecipe(any()))
					.thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,
							"Portions must be greater than 0."));

			UserSpecificRecipe requestBody = new UserSpecificRecipe();
			requestBody.setPortions(0);

			mockMvc.perform(post("/user-specific-recipes")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(requestBody)))
					.andExpect(status().isBadRequest())
					.andExpect(status().reason("Portions must be greater than 0."));
		}
	}

	@Nested
	@DisplayName("PUT /user-specific-recipes/{id}")
	class UpdateTests {

		@Test
		@DisplayName("should return 200 OK and the updated entity for valid input")
		void testUpdateSuccess() throws Exception {
			UserSpecificRecipe requestBody = new UserSpecificRecipe();
			requestBody.setPortions(5);

			UserSpecificRecipe updated = new UserSpecificRecipe();
			updated.setId(20L);
			updated.setPortions(5);

			when(userSpecificRecipeService.updateUserSpecificRecipe(eq(20L), any(UserSpecificRecipe.class)))
					.thenReturn(updated);

			mockMvc.perform(put("/user-specific-recipes/20")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(requestBody)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(20L))
					.andExpect(jsonPath("$.portions").value(5));
		}

		@Test
		@DisplayName("should return 404 NOT_FOUND if entity doesn't exist")
		void testUpdateNotFound() throws Exception {
			when(userSpecificRecipeService.updateUserSpecificRecipe(eq(999L), any()))
					.thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Not found"));

			mockMvc.perform(put("/user-specific-recipes/999")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(new UserSpecificRecipe())))
					.andExpect(status().isNotFound())
					.andExpect(status().reason("Not found"));
		}

		@Test
		@DisplayName("should return 400 BAD_REQUEST if portions <= 0")
		void testUpdatePortionsBadRequest() throws Exception {
			when(userSpecificRecipeService.updateUserSpecificRecipe(eq(5L), any()))
					.thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,
							"Portions must be greater than 0."));

			UserSpecificRecipe requestBody = new UserSpecificRecipe();
			requestBody.setPortions(0);

			mockMvc.perform(put("/user-specific-recipes/5")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(requestBody)))
					.andExpect(status().isBadRequest())
					.andExpect(status().reason("Portions must be greater than 0."));
		}
	}

	@Nested
	@DisplayName("DELETE /user-specific-recipes/{id}")
	class DeleteTests {

		@Test
		@DisplayName("should return 204 NO_CONTENT if deleted successfully")
		void testDeleteSuccess() throws Exception {
			doNothing().when(userSpecificRecipeService).deleteUserSpecificRecipe(1L);

			mockMvc.perform(delete("/user-specific-recipes/1"))
					.andExpect(status().isNoContent());
		}

		@Test
		@DisplayName("should return 404 NOT_FOUND if entity doesn't exist")
		void testDeleteNotFound() throws Exception {
			doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Not found"))
					.when(userSpecificRecipeService).deleteUserSpecificRecipe(999L);

			mockMvc.perform(delete("/user-specific-recipes/999"))
					.andExpect(status().isNotFound())
					.andExpect(status().reason("Not found"));
		}
	}

	@Nested
	@DisplayName("GET /user-specific-recipes/day/{dayId}")
	class GetAllByDayTests {
		@Test
		@DisplayName("should return list of USRs for a given day")
		void testGetAllByDay() throws Exception {
			UserSpecificRecipe usr = new UserSpecificRecipe();
			usr.setId(2L);
			usr.setPortions(3);

			when(userSpecificRecipeService.getAllByDay(10L))
					.thenReturn(List.of(usr));

			mockMvc.perform(get("/user-specific-recipes/day/10"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].id").value(2L))
					.andExpect(jsonPath("$[0].portions").value(3));
		}

		@Test
		@DisplayName("should return empty list if none found for day")
		void testGetAllByDayEmpty() throws Exception {
			when(userSpecificRecipeService.getAllByDay(99L))
					.thenReturn(new ArrayList<>());

			mockMvc.perform(get("/user-specific-recipes/day/99"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.length()").value(0));
		}
	}
}
