package com.github.piotrostrow.chess.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.Game;
import com.github.piotrostrow.chess.domain.chess.GameResult;
import com.github.piotrostrow.chess.repository.UserRepository;
import com.github.piotrostrow.chess.rest.dto.UserDto;
import com.github.piotrostrow.chess.rest.serivce.GameService;
import com.github.piotrostrow.chess.rest.serivce.UserService;
import com.github.piotrostrow.chess.security.JwtTokenUtil;
import com.github.piotrostrow.chess.security.Role;
import com.github.piotrostrow.chess.security.UserDetailsImpl;
import com.github.piotrostrow.chess.ws.game.GameSession;
import com.github.piotrostrow.chess.ws.game.Player;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private GameService gameService;

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
				.andExpect(status().is(HttpStatus.CREATED.value()))
				.andExpect(jsonPath("$.password").doesNotExist())
				.andExpect(jsonPath("$.username", is(content.get("username"))))
				.andExpect(jsonPath("$.email", is(content.get("email"))));
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
				.andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
				.andExpect(jsonPath("$.message", is("Password must have a minimum length of 6")));
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
				.andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
				.andExpect(jsonPath("$.message", is("email: must be a well-formed email address")));
	}

	@Test
	void testCreateUserMalformedJsonShouldReturn400WithReadableMessage() throws Exception {
		String content = "{\"username\" \"malformed\"}";

		mockMvc.perform(post("/users/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(content))
				.andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
				.andExpect(jsonPath("$.message", is("Malformed request body")));
	}

	@Test
	void testGetAllUsersNotAuthenticatedShouldReturn401() throws Exception {
		mockMvc.perform(get("/users/"))
				.andExpect(status().is(UNAUTHORIZED.value()));
	}

	@Test
	void testGetAllUsersNotAuthorizedShouldReturn403() throws Exception {
		mockMvc.perform(
				get("/users/").header(AUTHORIZATION, "Bearer " + tokenWithUserAuthorities())
		).andExpect(status().is(FORBIDDEN.value()));
	}

	@Test
	@DirtiesContext
	void testGetAllUsersAuthorizedShouldReturn200WithUsers() throws Exception {
		userService.createUser(new UserDto("user123", "password", "user@email.com"));

		// TODO: assert response
		mockMvc.perform(
				get("/users/").header(AUTHORIZATION, "Bearer " + tokenWithAdminAuthority())
		).andExpect(status().isOk());
	}

	@Test
	void testGetUserByUsernameNotAuthenticatedShouldReturn401() throws Exception {
		mockMvc.perform(get("/users/username123/"))
				.andExpect(status().is(UNAUTHORIZED.value()));
	}

	@Test
	@DirtiesContext
	void testGetUserByUsernameAuthorizedShouldReturn200WithUser() throws Exception {
		UserDto userDto = new UserDto("user123", "password", "user@email.com");
		UserDto expected = userService.createUser(userDto);
		expected.setPassword(null);

		String contentAsString = mockMvc.perform(
						get("/users/{name}/", userDto.getUsername())
								.header(AUTHORIZATION, "Bearer " + tokenWithUserAuthorities())
				).andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		UserDto actual = objectMapper.readValue(contentAsString, UserDto.class);

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void testGetUserByUsernameAuthorizedUserDoesNotExistShouldReturn404() throws Exception {
		mockMvc.perform(
				get("/users/user12345/")
						.header(AUTHORIZATION, "Bearer " + tokenWithUserAuthorities())
		).andExpect(status().isNotFound());
	}

	@Test
	void testDeleteUserNotAuthorizedShouldReturn403() throws Exception {
		mockMvc.perform(
				delete("/users/user12345/")
						.header(AUTHORIZATION, "Bearer " + tokenWithUserAuthorities())
		).andExpect(status().is(FORBIDDEN.value()));
	}

	@Test
	@DirtiesContext
	void testDeleteUserAuthorizedShouldReturn203NoContent() throws Exception {
		UserDto userDto = new UserDto("user123", "password", "user123@email.com");
		userService.createUser(userDto);

		mockMvc.perform(
				delete("/users/{name}/", userDto.getUsername())
						.header(AUTHORIZATION, "Bearer " + tokenWithAdminAuthority())
		).andExpect(status().is(NO_CONTENT.value()));

		assertThat(userRepository.findAll()).isEmpty();
	}

	@Test
	void testDeleteUserAuthorizedUserDoesNotExistShouldReturn404() throws Exception {
		mockMvc.perform(
				delete("/users/user123/")
						.header(AUTHORIZATION, "Bearer " + tokenWithAdminAuthority())
		).andExpect(status().isNotFound());
	}

	@Test
	@DirtiesContext
	void testDeleteUserWithGamesPlayedGamesShouldBeRemoved() throws Exception {
		List<UserDto> users = Stream.of(
				new UserDto("user123", "password", "user123@email.com"),
				new UserDto("user321", "password", "user321@email.com")
		).map(userService::createUser).collect(Collectors.toList());

		GameSession gameSession = Mockito.mock(GameSession.class);
		when(gameSession.getGame()).thenReturn(new Game());
		when(gameSession.getGameResult()).thenReturn(GameResult.ONGOING);
		when(gameSession.getWhite()).thenReturn(new Player("user123", Color.WHITE, 10));
		when(gameSession.getBlack()).thenReturn(new Player("user321", Color.BLACK, 10));
		when(gameSession.getWinner()).thenReturn(Optional.empty());

		gameService.saveGame(gameSession);

		assertThat(gameService.getGames()).hasSize(1);

		mockMvc.perform(
				delete("/users/{username}/", users.get(0).getUsername())
						.header(AUTHORIZATION, "Bearer " + tokenWithAdminAuthority())
		).andExpect(status().isNoContent());

		assertThat(userService.getAllUsers()).hasSize(1).contains(users.get(1));
		assertThat(gameService.getGames()).isEmpty();
		assertThat(gameService.getGamesForUser(users.get(1).getUsername())).isEmpty();
	}

	private String tokenWithUserAuthorities() {
		return jwtTokenUtil.generateAccessToken(new UserDetailsImpl("user321", "", List.of(new SimpleGrantedAuthority(Role.USER.toString()))));
	}

	private String tokenWithAdminAuthority() {
		List<GrantedAuthority> authorities = Arrays.stream(Role.values())
				.map(Role::toString)
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		return jwtTokenUtil.generateAccessToken(new UserDetailsImpl("user123", "", authorities));
	}
}