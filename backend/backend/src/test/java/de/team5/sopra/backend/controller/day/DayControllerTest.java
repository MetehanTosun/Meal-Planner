package de.team5.sopra.backend.controller.day;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.team5.sopra.backend.controller.DayController;
import de.team5.sopra.backend.dto.AddRecipeToDayRequest;
import de.team5.sopra.backend.dto.DayRequest;
import de.team5.sopra.backend.models.Day;
import de.team5.sopra.backend.service.DayService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DayController.class)
class DayControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private DayService dayService;

	@BeforeEach
	void setup() {
		// Setup mock data or common configurations if required.
	}

	@Nested
	@DisplayName("GET /days")
	class GetAllDaysTests {

		@Test
		@DisplayName("should return 200 OK and a list of days")
		void testGetAllDays_Success() throws Exception {
			Day day1 = new Day();
			day1.setId(1L);
			day1.setDate(new Date());

			when(dayService.getAllDays()).thenReturn(List.of(day1));

			mockMvc.perform(get("/days"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].id").value(1L));
		}

		@Test
		@DisplayName("should return 200 OK and an empty list when no days exist")
		void testGetAllDays_Empty() throws Exception {
			when(dayService.getAllDays()).thenReturn(Collections.emptyList());

			mockMvc.perform(get("/days"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.length()").value(0));
		}
	}

	@Nested
	@DisplayName("GET /days/{id}")
	class GetDayByIdTests {

		@Test
		@DisplayName("should return 200 OK and the day for a valid ID")
		void testGetDayById_Success() throws Exception {
			Day day = new Day();
			day.setId(10L);
			day.setDate(new Date());

			when(dayService.getDayById(10L)).thenReturn(day);

			mockMvc.perform(get("/days/10"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(10L));
		}

		@Test
		@DisplayName("should return 404 NOT_FOUND when day does not exist")
		void testGetDayById_NotFound() throws Exception {
			when(dayService.getDayById(999L))
					.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Day not found with ID: 999"));

			mockMvc.perform(get("/days/999"))
					.andExpect(status().isNotFound());
		}

	}

	@Nested
	@DisplayName("POST /days")
	class CreateDayTests {

		@Test
		@DisplayName("should return 201 CREATED when a day is created successfully")
		void testCreateDay_Success() throws Exception {
			DayRequest request = new DayRequest();
			request.setDate(new Date());
			request.setWeekId(2L);

			Day createdDay = new Day();
			createdDay.setId(100L);

			when(dayService.createDay(ArgumentMatchers.any(DayRequest.class))).thenReturn(createdDay);

			mockMvc.perform(post("/days")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request)))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.id").value(100L));
		}

		@Test
		@DisplayName("should return 404 NOT_FOUND when week ID is invalid")
		void testCreateDay_WeekNotFound() throws Exception {
			DayRequest request = new DayRequest();
			request.setDate(new Date());
			request.setWeekId(999L);

			when(dayService.createDay(any(DayRequest.class)))
					.thenThrow(new EntityNotFoundException("Week not found with ID: 999"));

			mockMvc.perform(post("/days")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request)))
					.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("DELETE /days/{id}")
	class DeleteDayTests {

		@Test
		@DisplayName("should return 204 NO_CONTENT when the day is deleted successfully")
		void testDeleteDay_Success() throws Exception {
			doNothing().when(dayService).deleteDay(1L);

			mockMvc.perform(delete("/days/1"))
					.andExpect(status().isNoContent());

			verify(dayService, times(1)).deleteDay(1L);
		}

		@Test
		@DisplayName("should return 404 NOT_FOUND when the day does not exist")
		void testDeleteDay_NotFound() throws Exception {
			doThrow(new EntityNotFoundException("Day not found with ID: 999"))
					.when(dayService).deleteDay(999L);

			mockMvc.perform(delete("/days/999"))
					.andExpect(status().isNotFound());
		}
	}
	@Nested
	@DisplayName("POST /days/{dayId}/add-recipe")
	class AddRecipeToDayTests {

		@Test
		@DisplayName("should return 200 OK when recipe is added successfully to the day")
		void testAddRecipeToDay_Success() throws Exception {
			AddRecipeToDayRequest request = new AddRecipeToDayRequest();
			request.setRecipeId(1L);
			request.setDayId(1L); // Not strictly necessary, but keeping consistent with the DTO
			request.setPortions(3);

			Day day = new Day();
			day.setId(1L);

			when(dayService.addRecipeToDayWithPortions(eq(1L), eq(1L), eq(3))).thenReturn(day);

			mockMvc.perform(post("/days/1/add-recipe")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(1L));
		}

		@Test
		@DisplayName("should return 404 NOT_FOUND when the day does not exist")
		void testAddRecipeToDay_DayNotFound() throws Exception {
			AddRecipeToDayRequest request = new AddRecipeToDayRequest();
			request.setRecipeId(1L);
			request.setDayId(999L); // Matches the path parameter
			request.setPortions(3);

			when(dayService.addRecipeToDayWithPortions(eq(999L), eq(1L), eq(3)))
					.thenThrow(new EntityNotFoundException("Day not found with id: 999"));

			mockMvc.perform(post("/days/999/add-recipe")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request)))
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("should return 404 NOT_FOUND when the recipe does not exist")
		void testAddRecipeToDay_RecipeNotFound() throws Exception {
			AddRecipeToDayRequest request = new AddRecipeToDayRequest();
			request.setRecipeId(999L);
			request.setDayId(1L);
			request.setPortions(3);

			when(dayService.addRecipeToDayWithPortions(eq(1L), eq(999L), eq(3)))
					.thenThrow(new EntityNotFoundException("Recipe not found with id: 999"));

			mockMvc.perform(post("/days/1/add-recipe")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request)))
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("should return 400 BAD_REQUEST when portions are invalid")
		void testAddRecipeToDay_InvalidPortions() throws Exception {
			AddRecipeToDayRequest request = new AddRecipeToDayRequest();
			request.setRecipeId(1L);
			request.setDayId(1L);
			request.setPortions(0); // Invalid portions

			mockMvc.perform(post("/days/1/add-recipe")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request)))
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("should return 400 BAD_REQUEST when request is invalid")
		void testAddRecipeToDay_InvalidRequest() throws Exception {
			AddRecipeToDayRequest request = new AddRecipeToDayRequest();
			request.setRecipeId(null); // Missing required field
			request.setDayId(1L);
			request.setPortions(3);

			mockMvc.perform(post("/days/1/add-recipe")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request)))
					.andExpect(status().isBadRequest());
		}
	}

}
