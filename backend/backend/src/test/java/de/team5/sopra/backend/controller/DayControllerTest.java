package de.team5.sopra.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.team5.sopra.backend.dto.DayRequest;
import de.team5.sopra.backend.models.Day;
import de.team5.sopra.backend.service.DayService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DayController.class)
class DayControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DayService dayService;

	@Autowired
	private ObjectMapper objectMapper;

	@Nested
	@DisplayName("GET /days")
	class GetAllDaysTests {

		@Test
		@DisplayName("should return 200 OK and a list of Days")
		void testGetAllDays() throws Exception {
			Day d1 = new Day();
			d1.setId(1L);

			when(dayService.getAllDays()).thenReturn(List.of(d1));

			mockMvc.perform(get("/days"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].id").value(1L));
		}

		@Test
		@DisplayName("should return empty list when no Days exist")
		void testGetAllDaysEmpty() throws Exception {
			when(dayService.getAllDays()).thenReturn(new ArrayList<>());

			mockMvc.perform(get("/days"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.length()").value(0));
		}
	}

	@Nested
	@DisplayName("GET /days/{id}")
	class GetDayByIdTests {

		@Test
		@DisplayName("should return 200 OK and the specified Day")
		void testGetDayByIdSuccess() throws Exception {
			Day day = new Day();
			day.setId(10L);

			when(dayService.getDayById(10L)).thenReturn(day);

			mockMvc.perform(get("/days/10"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(10L));
		}

		@Test
		@DisplayName("should return 404 NOT_FOUND when Day does not exist")
		void testGetDayByIdNotFound() throws Exception {
			doThrow(new ResponseStatusException(
					org.springframework.http.HttpStatus.NOT_FOUND,
					"The day with the ID: 999 wasn't found."
			)).when(dayService).getDayById(999L);

			mockMvc.perform(get("/days/999"))
					.andExpect(status().isNotFound())
					.andExpect(status().reason("The day with the ID: 999 wasn't found."));
		}
	}

	@Nested
	@DisplayName("DELETE /days/{id}")
	class DeleteDayTests {

		@Test
		@DisplayName("should return 204 NO_CONTENT if Day was deleted successfully")
		void testDeleteDaySuccess() throws Exception {
			doNothing().when(dayService).deleteDay(1L);

			mockMvc.perform(delete("/days/1"))
					.andExpect(status().isNoContent());

			verify(dayService, times(1)).deleteDay(1L);
		}

		@Test
		@DisplayName("should return 404 NOT_FOUND if Day does not exist")
		void testDeleteDayNotFound() throws Exception {
			doThrow(new EntityNotFoundException("Day not found with id: 999"))
					.when(dayService).deleteDay(999L);

			mockMvc.perform(delete("/days/999"))
					.andExpect(status().isNotFound())
					.andExpect(status().reason("Day not found with id: 999"));
		}
	}

	@Nested
	@DisplayName("DELETE /days/{dayId}/recipes/{recipeId}")
	class RemoveRecipeFromDayTests {

		@Test
		@DisplayName("should return 200 OK and success message if recipe is removed")
		void testRemoveRecipeSuccess() throws Exception {
			doNothing().when(dayService).removeRecipeFromDay(2L, 100L);

			mockMvc.perform(delete("/days/2/recipes/100"))
					.andExpect(status().isOk())
					.andExpect(content().string("Recipe successfully removed from the day."));
		}

		@Test
		@DisplayName("should return 404 NOT_FOUND if day or recipe does not exist")
		void testRemoveRecipeNotFound() throws Exception {
			doThrow(new EntityNotFoundException("Day not found with id: 3"))
					.when(dayService).removeRecipeFromDay(3L, 100L);

			mockMvc.perform(delete("/days/3/recipes/100"))
					.andExpect(status().isNotFound())
					.andExpect(content().string("Day not found with id: 3"));
		}

		@Test
		@DisplayName("should return 500 INTERNAL_SERVER_ERROR if other exception occurs")
		void testRemoveRecipeServerError() throws Exception {
			doThrow(new RuntimeException("Some internal error"))
					.when(dayService).removeRecipeFromDay(4L, 100L);

			mockMvc.perform(delete("/days/4/recipes/100"))
					.andExpect(status().isInternalServerError())
					.andExpect(content().string("An error occurred."));
		}
	}

	@Nested
	@DisplayName("POST /days")
	class CreateDayTests {

		@Test
		@DisplayName("should return 201 CREATED if Day is created successfully")
		void testCreateDaySuccess() throws Exception {
			DayRequest request = new DayRequest();
			request.setDate(new Date());
			request.setWeekId(2L);
			request.setRecipeIds(List.of(10L, 11L));

			Day createdDay = new Day();
			createdDay.setId(100L);

			when(dayService.createDay(ArgumentMatchers.any(DayRequest.class)))
					.thenReturn(createdDay);

			mockMvc.perform(post("/days")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request)))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.id").value(100L));
		}

		@Test
		@DisplayName("should return 404 NOT_FOUND if weekId is invalid")
		void testCreateDayWeekNotFound() throws Exception {
			DayRequest request = new DayRequest();
			request.setDate(new Date());
			request.setWeekId(999L);

			doThrow(new EntityNotFoundException("Week not found with id: 999"))
					.when(dayService).createDay(any(DayRequest.class));

			mockMvc.perform(post("/days")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request)))
					.andExpect(status().isNotFound())
					.andExpect(content().string(""));
		}

		@Test
		@DisplayName("should return 500 INTERNAL_SERVER_ERROR for other exceptions")
		void testCreateDayServerError() throws Exception {
			DayRequest request = new DayRequest();
			request.setDate(new Date());

			doThrow(new RuntimeException("Some internal error"))
					.when(dayService).createDay(any(DayRequest.class));

			mockMvc.perform(post("/days")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request)))
					.andExpect(status().isInternalServerError());
		}
	}

	@Nested
	@DisplayName("PUT /days/{id}")
	class UpdateDayTests {

		@Test
		@DisplayName("should return 200 OK and updated Day if successful")
		void testUpdateDaySuccess() throws Exception {
			DayRequest request = new DayRequest();
			request.setDate(new Date());
			request.setRecipeIds(List.of(20L, 21L));

			Day updatedDay = new Day();
			updatedDay.setId(55L);

			when(dayService.updateDay(eq(55L), any(DayRequest.class)))
					.thenReturn(updatedDay);

			mockMvc.perform(put("/days/55")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(55L));
		}

		@Test
		@DisplayName("should return 404 NOT_FOUND if Day not found")
		void testUpdateDayNotFound() throws Exception {
			DayRequest request = new DayRequest();
			request.setDate(new Date());

			doThrow(new EntityNotFoundException("Day not found with id: 999"))
					.when(dayService).updateDay(eq(999L), any(DayRequest.class));

			mockMvc.perform(put("/days/999")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request)))
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("should return 500 INTERNAL_SERVER_ERROR if exception occurs")
		void testUpdateDayServerError() throws Exception {
			DayRequest request = new DayRequest();
			request.setDate(new Date());

			doThrow(new RuntimeException("Some server error"))
					.when(dayService).updateDay(eq(10L), any(DayRequest.class));

			mockMvc.perform(put("/days/10")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request)))
					.andExpect(status().isInternalServerError());
		}
	}
}
