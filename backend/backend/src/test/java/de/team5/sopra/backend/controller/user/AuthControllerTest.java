package de.team5.sopra.backend.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.team5.sopra.backend.controller.AuthController;
import de.team5.sopra.backend.models.User;
import de.team5.sopra.backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("Should successfully register a user")
	void testRegisterUserSuccess() throws Exception {
		User mockUser = new User();
		mockUser.setId(1L);
		mockUser.setUsername("testuser");

		Mockito.when(userService.registerUser(eq("testuser"), eq("password123"))).thenReturn(mockUser);

		String requestBody = objectMapper.writeValueAsString(Map.of(
				"username", "testuser",
				"password", "password123"
		));

		mockMvc.perform(post("/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.userId").value(1L))
				.andExpect(jsonPath("$.message").value("Registration successful"));
	}

	@Test
	@DisplayName("Should fail registration for existing user")
	void testRegisterUserConflict() throws Exception {
		Mockito.when(userService.registerUser(eq("testuser"), eq("password123")))
				.thenThrow(new RuntimeException("User already exists"));

		String requestBody = objectMapper.writeValueAsString(Map.of(
				"username", "testuser",
				"password", "password123"
		));

		mockMvc.perform(post("/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("User already exists"));
	}

	@Test
	@DisplayName("Should successfully authenticate a user")
	void testLoginUserSuccess() throws Exception {
		User mockUser = new User();
		mockUser.setId(1L);
		mockUser.setUsername("testuser");

		Mockito.when(userService.authenticateUser(eq("testuser"), eq("password123"))).thenReturn(mockUser);

		String requestBody = objectMapper.writeValueAsString(Map.of(
				"username", "testuser",
				"password", "password123"
		));

		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.userId").value(1L))
				.andExpect(jsonPath("$.message").value("Login successful"));
	}

	@Test
	@DisplayName("Should fail login with invalid credentials")
	void testLoginUserInvalidCredentials() throws Exception {
		Mockito.when(userService.authenticateUser(eq("testuser"), eq("wrongpassword")))
				.thenReturn(null);

		String requestBody = objectMapper.writeValueAsString(Map.of(
				"username", "testuser",
				"password", "wrongpassword"
		));

		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Invalid credentials"));
	}
}