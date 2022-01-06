package com.github.piotrostrow.chess.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void testCreateUser() throws Exception {
		Map<String, String> content = Map.of(
				"username", "username123",
				"password", "qwerty",
				"email", "john.smith@email.com"
		);

		mockMvc.perform(post("/users/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(content)))
				.andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist())
				.andExpect(MockMvcResultMatchers.jsonPath("$.username", is(content.get("username"))))
				.andExpect(MockMvcResultMatchers.jsonPath("$.email", is(content.get("email"))));
	}

	@Test
	void testCreateUserNoPasswordShouldReturn400BadRequest() throws Exception {
		Object content = Map.of(
				"username", "username123",
				"email", "john.smith@email.com"
		);

		mockMvc.perform(post("/users/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(content)))
				.andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.message", is("Password must have a minimum length of 6")));
	}

	@Test
	void testCreateUserInvalidEmailShouldReturn400() throws Exception {
		Object content = Map.of(
				"username", "username123",
				"password", "qwerty",
				"email", "john.smith.com"
		);

		mockMvc.perform(post("/users/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(content)))
				.andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.message", is("email: must be a well-formed email address")));
	}

	@Test
	void testCreateUserMalformedJsonShouldReturn400WithReadableMessage() throws Exception {
		String content = "{\"username\" \"malformed\"}";

		mockMvc.perform(post("/users/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(content))
				.andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.message", is("Malformed request body")));
	}

	@Test
	void testGetAllUsersNotAuthenticatedShouldReturn401() throws Exception {
		mockMvc.perform(get("/users/"))
				.andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()));
	}

	@Test
	void testGetUserByUsernameNotAuthenticatedShouldReturn401() throws Exception {
		mockMvc.perform(get("/users/username123"))
				.andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()));
	}
}