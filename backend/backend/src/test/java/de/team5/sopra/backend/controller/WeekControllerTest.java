package de.team5.sopra.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.team5.sopra.backend.dto.WeekRequest;
import de.team5.sopra.backend.dto.WeeklyCreationDTO;
import de.team5.sopra.backend.models.User;
import de.team5.sopra.backend.models.Week;
import de.team5.sopra.backend.repository.DayRepository;
import de.team5.sopra.backend.repository.UserRepository;
import de.team5.sopra.backend.repository.WeekRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class WeekControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WeekRepository weekRepository;

	@Autowired
	private DayRepository dayRepository;

	@Autowired
	private UserRepository userRepository;

	private String uniqueUsername = "testUser_" + System.currentTimeMillis();

	@BeforeEach
	void setup() {
		System.out.println("=== Starting new test ===");
	}

	@AfterEach
	void tearDown() {
		System.out.println(">>> Tearing down DB after test");
		weekRepository.deleteAll();
		dayRepository.deleteAll();
		userRepository.deleteAll();
		System.out.println(">>> Teardown complete");
	}


	@Test
	void testGetAllWeeks_EmptyInitially() throws Exception {
		System.out.println(">>> Running testGetAllWeeks_EmptyInitially");
		mockMvc.perform(get("/weeks"))
				.andExpect(status().isOk())
				.andExpect(content().json("[]"));
		System.out.println("<<< Finished testGetAllWeeks_EmptyInitially");
	}

	@Test
	void testGetWeek_NotFound() throws Exception {
		System.out.println(">>> Running testGetWeek_NotFound");
		mockMvc.perform(get("/weeks/999999"))
				.andExpect(status().isNotFound());
		System.out.println("<<< Finished testGetWeek_NotFound");
	}

	@Test
	void testCreateWeek_Success() throws Exception {
		System.out.println(">>> Running testCreateWeek_Success");
		User user = new User();
		user.setUsername(uniqueUsername);
		user.setPassword("dummyPass");
		userRepository.save(user);
		System.out.println("Created user with ID = " + user.getId());


		WeekRequest request = new WeekRequest();
		request.setUserId(user.getId());

		Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse("2025-01-01");
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DAY_OF_YEAR, 6);
		Date endDate = cal.getTime();

		request.setStartDate(startDate);
		request.setEndDate(endDate);

		System.out.println("Requesting POST /weeks with userId=" + request.getUserId()
				+ ", start=" + startDate + ", end=" + endDate);

		mockMvc.perform(post("/weeks")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.startDate").exists())
				.andExpect(jsonPath("$.endDate").exists());

		System.out.println("<<< Finished testCreateWeek_Success");
	}

	@Test
	void testCreateWeek_WithInvalidDayIds() throws Exception {
		System.out.println(">>> Running testCreateWeek_WithInvalidDayIds");

		User user = new User();
		user.setUsername(uniqueUsername);
		user.setPassword("testPass");
		userRepository.save(user);


		WeekRequest request = new WeekRequest();
		request.setUserId(user.getId());
		request.setStartDate(new Date());
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, 6);
		request.setEndDate(c.getTime());
		request.setDays(java.util.List.of(999999L));

		System.out.println("Posting week with invalid day IDs: " + request.getDays());

		mockMvc.perform(post("/weeks")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());

		System.out.println("<<< Finished testCreateWeek_WithInvalidDayIds");
	}

	@Test
	void testGetWeek_Found() throws Exception {
		System.out.println(">>> Running testGetWeek_Found");

		User user = new User();
		user.setUsername(uniqueUsername);
		user.setPassword("pw123");
		userRepository.save(user);
		System.out.println("Saved user with ID=" + user.getId());

		Week week = new Week();
		week.setUser(user);
		week.setStartDate(new Date());
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 6);
		week.setEndDate(cal.getTime());
		Week saved = weekRepository.save(week);
		System.out.println("Saved week with ID=" + saved.getId() + " referencing user " + user.getId());

		mockMvc.perform(get("/weeks/" + saved.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(saved.getId()));

		System.out.println("<<< Finished testGetWeek_Found");
	}

	@Test
	void testDeleteWeek_Success() throws Exception {
		System.out.println(">>> Running testDeleteWeek_Success");

		User user = new User();
		user.setUsername("deleteUser");
		user.setPassword("dummyPass"); // Non-null
		userRepository.save(user);
		System.out.println("User created with ID=" + user.getId());

		Week week = new Week();
		week.setUser(user);
		week.setStartDate(new Date());
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 6);
		week.setEndDate(cal.getTime());
		Week saved = weekRepository.save(week);
		System.out.println("Week created with ID=" + saved.getId());

		mockMvc.perform(delete("/weeks/" + saved.getId()))
				.andExpect(status().isNoContent());

		boolean stillExists = weekRepository.existsById(saved.getId());
		System.out.println("Does the week still exist in DB? " + stillExists);

		assertThat(stillExists).isFalse();
		System.out.println("<<< Finished testDeleteWeek_Success");
	}

	@Test
	void testDeleteWeek_NotFound() throws Exception {
		System.out.println(">>> Running testDeleteWeek_NotFound");
		mockMvc.perform(delete("/weeks/12345"))
				.andExpect(status().isNotFound());
		System.out.println("<<< Finished testDeleteWeek_NotFound");
	}

	/*
	Create New Default Week Tests
	 */


	@Test
	void testCreateDefaultNewWeek_Success() throws Exception {
		System.out.println(">>> Running testCreateDefaultNewWeek_Success");

		User user = new User();
		user.setUsername("testUser");
		user.setPassword("password123");
		userRepository.save(user);

		System.out.println("Created user with ID = " + user.getId());

		String startDate = "2024-12-30T00:00:00.000+00:00";
		WeeklyCreationDTO weeklyCreationDTO = new WeeklyCreationDTO();
		weeklyCreationDTO.setStartDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(startDate));

		mockMvc.perform(post("/weeks/create/user/{id}", user.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(weeklyCreationDTO)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.startDate").value("2024-12-30T00:00:00.000+00:00"))
				.andExpect(jsonPath("$.endDate").value("2025-01-05T00:00:00.000+00:00"))
				.andExpect(jsonPath("$.days").isArray())
				.andExpect(jsonPath("$.days.length()").value(7))
				.andExpect(jsonPath("$.days[0].date").value("2024-12-30T00:00:00.000+00:00"))
				.andExpect(jsonPath("$.days[1].date").value("2024-12-31T00:00:00.000+00:00"))
				.andExpect(jsonPath("$.days[2].date").value("2025-01-01T00:00:00.000+00:00"))
				.andExpect(jsonPath("$.days[3].date").value("2025-01-02T00:00:00.000+00:00"))
				.andExpect(jsonPath("$.days[4].date").value("2025-01-03T00:00:00.000+00:00"))
				.andExpect(jsonPath("$.days[5].date").value("2025-01-04T00:00:00.000+00:00"))
				.andExpect(jsonPath("$.days[6].date").value("2025-01-05T00:00:00.000+00:00"))
				.andExpect(jsonPath("$.days[0].recipes").isArray())
				.andExpect(jsonPath("$.days[0].recipes").isEmpty());

		System.out.println("<<< Finished testCreateDefaultNewWeek_Success");
	}
}
