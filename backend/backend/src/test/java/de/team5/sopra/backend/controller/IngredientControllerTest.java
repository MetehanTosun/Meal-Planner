package de.team5.sopra.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.team5.sopra.backend.models.Ingredient;
import de.team5.sopra.backend.models.Ingredient.Unit;
import de.team5.sopra.backend.controller.general.IngredientController;
import de.team5.sopra.backend.service.general.IngredientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = IngredientController.class)
class IngredientControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private IngredientService ingredientService;

	@Nested
	@DisplayName("POST /ingredients")
	class CreateIngredientTests {

		@Test
		@DisplayName("soll bei gültigen Daten 201 CREATED zurückgeben")
		void testCreateIngredient_Success() throws Exception {
			Ingredient input = new Ingredient();
			input.setName("Tomatoes");
			input.setAmount(300.0);
			input.setUnit(Unit.G);

			Ingredient saved = new Ingredient();
			saved.setId(1L);
			saved.setName("Tomatoes");
			saved.setAmount(300.0);
			saved.setUnit(Unit.G);

			when(ingredientService.createIngredient(ArgumentMatchers.any(Ingredient.class)))
					.thenReturn(saved);

			mockMvc.perform(post("/ingredients")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(input)))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.id").value(1L))
					.andExpect(jsonPath("$.name").value("Tomatoes"))
					.andExpect(jsonPath("$.amount").value(300.0))
					.andExpect(jsonPath("$.unit").value("G"));
		}

		@Test
		@DisplayName("soll bei falschen Eingaben 400 BAD_REQUEST zurückgeben")
		void testCreateIngredient_BadRequest() throws Exception {

			when(ingredientService.createIngredient(ArgumentMatchers.any(Ingredient.class)))
					.thenThrow(new IllegalArgumentException("Name darf nicht leer sein!"));

			Ingredient invalid = new Ingredient();

			mockMvc.perform(post("/ingredients")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(invalid)))
					.andExpect(status().isBadRequest())
					.andExpect(content().string("Name darf nicht leer sein!"));
		}

		@Test
		@DisplayName("soll bei sonstigem Fehler 500 INTERNAL_SERVER_ERROR zurückgeben")
		void testCreateIngredient_InternalError() throws Exception {
			when(ingredientService.createIngredient(any()))
					.thenThrow(new RuntimeException("Unerwarteter Fehler"));

			Ingredient input = new Ingredient();
			input.setName("Tomatoes");
			input.setAmount(300.0);
			input.setUnit(Unit.G);

			mockMvc.perform(post("/ingredients")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(input)))
					.andExpect(status().isInternalServerError())
					.andExpect(content().string("Unerwarteter Fehler"));
		}
	}

	@Nested
	@DisplayName("GET /ingredients")
	class GetAllIngredientsTests {

		@Test
		@DisplayName("soll eine Liste von Ingredients zurückgeben (200 OK)")
		void testGetAllIngredients() throws Exception {
			Ingredient ing1 = new Ingredient();
			ing1.setId(1L);
			ing1.setName("Tomatoes");
			ing1.setAmount(300.0);
			ing1.setUnit(Unit.G);

			when(ingredientService.getAllIngredients())
					.thenReturn(List.of(ing1));

			mockMvc.perform(get("/ingredients"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].id").value(1L))
					.andExpect(jsonPath("$[0].name").value("Tomatoes"));
		}

		@Test
		@DisplayName("soll leere Liste zurückgeben, wenn keine Ingredients vorhanden")
		void testGetAllIngredients_Empty() throws Exception {
			when(ingredientService.getAllIngredients())
					.thenReturn(Collections.emptyList());

			mockMvc.perform(get("/ingredients"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$").isArray())
					.andExpect(jsonPath("$.length()").value(0));
		}
	}

	@Nested
	@DisplayName("GET /ingredients/{id}")
	class GetIngredientByIdTests {

		@Test
		@DisplayName("soll Ingredient bei gültiger ID zurückgeben (200 OK)")
		void testGetIngredientById_Success() throws Exception {
			Ingredient ing = new Ingredient();
			ing.setId(2L);
			ing.setName("Onions");
			ing.setAmount(100.0);
			ing.setUnit(Unit.G);

			when(ingredientService.getIngredientById(2L)).thenReturn(ing);

			mockMvc.perform(get("/ingredients/2"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(2L))
					.andExpect(jsonPath("$.name").value("Onions"));
		}

		@Test
		@DisplayName("soll 404 NOT_FOUND zurückgeben, wenn Ingredient nicht existiert")
		void testGetIngredientById_NotFound() throws Exception {
			when(ingredientService.getIngredientById(999L))
					.thenThrow(new RuntimeException("Ingredient mit ID 999 wurde nicht gefunden."));

			mockMvc.perform(get("/ingredients/999"))
					.andExpect(status().isNotFound())
					.andExpect(content().string("Ingredient mit ID 999 wurde nicht gefunden."));
		}
	}

	@Nested
	@DisplayName("PUT /ingredients/{id}")
	class UpdateIngredientTests {

		@Test
		@DisplayName("soll Ingredient aktualisieren und 200 OK zurückgeben")
		void testUpdateIngredient_Success() throws Exception {
			Ingredient existing = new Ingredient();
			existing.setId(1L);
			existing.setName("OldName");
			existing.setAmount(100.0);
			existing.setUnit(Unit.G);

			Ingredient updated = new Ingredient();
			updated.setId(1L);
			updated.setName("NewName");
			updated.setAmount(200.0);
			updated.setUnit(Unit.ML);

			when(ingredientService.updateIngredient(eq(1L), any(Ingredient.class)))
					.thenReturn(updated);

			Ingredient dto = new Ingredient();
			dto.setName("NewName");
			dto.setAmount(200.0);
			dto.setUnit(Unit.ML);

			mockMvc.perform(put("/ingredients/1")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(dto)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.name").value("NewName"))
					.andExpect(jsonPath("$.amount").value(200.0))
					.andExpect(jsonPath("$.unit").value("ML"));
		}

		@Test
		@DisplayName("soll bei falschen Eingaben 400 BAD_REQUEST zurückgeben")
		void testUpdateIngredient_BadRequest() throws Exception {
			when(ingredientService.updateIngredient(eq(5L), any()))
					.thenThrow(new IllegalArgumentException("Amount muss > 0 sein!"));

			Ingredient dto = new Ingredient();
			dto.setAmount(-100); // ungültig

			mockMvc.perform(put("/ingredients/5")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(dto)))
					.andExpect(status().isBadRequest())
					.andExpect(content().string("Amount muss > 0 sein!"));
		}

		@Test
		@DisplayName("soll 404 NOT_FOUND zurückgeben, wenn Ingredient nicht existiert")
		void testUpdateIngredient_NotFound() throws Exception {
			when(ingredientService.updateIngredient(eq(999L), any()))
					.thenThrow(new RuntimeException("Ingredient nicht gefunden"));

			mockMvc.perform(put("/ingredients/999")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(new Ingredient())))
					.andExpect(status().isNotFound())
					.andExpect(content().string("Ingredient nicht gefunden"));
		}
	}

	@Nested
	@DisplayName("DELETE /ingredients/{id}")
	class DeleteIngredientTests {

		@Test
		@DisplayName("soll 204 NO_CONTENT zurückgeben, wenn Ingredient erfolgreich gelöscht")
		void testDeleteIngredient_Success() throws Exception {
			doNothing().when(ingredientService).deleteIngredient(1L);

			mockMvc.perform(delete("/ingredients/1"))
					.andExpect(status().isNoContent());
		}

		@Test
		@DisplayName("soll 404 NOT_FOUND zurückgeben, wenn Ingredient nicht existiert")
		void testDeleteIngredient_NotFound() throws Exception {
			doThrow(new RuntimeException("Ingredient nicht gefunden")).when(ingredientService).deleteIngredient(999L);

			mockMvc.perform(delete("/ingredients/999"))
					.andExpect(status().isNotFound())
					.andExpect(content().string("Ingredient nicht gefunden"));
		}
	}
}
