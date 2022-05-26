package com.github.piotrostrow.chess.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.piotrostrow.chess.repository.PuzzleRepository;
import com.github.piotrostrow.chess.rest.dto.PuzzleDto;
import com.github.piotrostrow.chess.rest.dto.PuzzleSolutionDto;
import com.github.piotrostrow.chess.rest.dto.UserDto;
import com.github.piotrostrow.chess.rest.serivce.PuzzleService;
import com.github.piotrostrow.chess.rest.serivce.UserService;
import com.github.piotrostrow.chess.security.Role;
import com.github.piotrostrow.chess.security.UserDetailsImpl;
import com.github.piotrostrow.chess.security.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class PuzzleControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private PuzzleRepository puzzleRepository;

	@Autowired
	private PuzzleService puzzleService;

	@Autowired
	private UserService userService;

	@Test
	void testGetRandomPuzzleNotAuthenticatedShouldReturn401() throws Exception {
		mockMvc.perform(get("/puzzles/random/")).andExpect(status().is(UNAUTHORIZED.value()));
	}

	@Test
	@DirtiesContext
	void testGetRandomPuzzleAuthenticatedShouldReturn404() throws Exception {
		userService.createUser(new UserDto("user123", "password", "user@email.com"));
		userService.createUser(new UserDto("user321", "password", "user@email.com"));

		mockMvc.perform(
				get("/puzzles/random/")
						.header(AUTHORIZATION, "Bearer " + tokenWithUserAuthorities())
		).andExpect(status().isNotFound());
	}

	@Test
	@DirtiesContext
	void testGetRandomPuzzleAuthenticatedShouldReturn200WithPuzzle() throws Exception {
		userService.createUser(new UserDto("user123", "password", "user@email.com"));
		userService.createUser(new UserDto("user321", "password", "user@email.com"));

		PuzzleDto expected = puzzleService.createPuzzle(new PuzzleDto("fen", List.of("e2e3"), 1000, List.of("Theme1")));

		String responseBody = mockMvc.perform(
						get("/puzzles/random/")
								.header(AUTHORIZATION, "Bearer " + tokenWithUserAuthorities())
				).andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		PuzzleDto actual = objectMapper.readValue(responseBody, PuzzleDto.class);

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void testSolvePuzzleNotAuthenticatedShouldReturn401() throws Exception {
		String content = objectMapper.writeValueAsString(new PuzzleSolutionDto());
		mockMvc.perform(
				post("/puzzles/solve/")
						.contentType(APPLICATION_JSON)
						.content(content)
		).andExpect(status().is(UNAUTHORIZED.value()));
	}

	@Test
	@DirtiesContext
	void testSolvePuzzleAuthenticatedShouldReturn404() throws Exception {
		userService.createUser(new UserDto("user123", "password", "user@email.com"));
		userService.createUser(new UserDto("user321", "password", "user@email.com"));

		mockMvc.perform(
				get("/puzzles/random/")
						.header(AUTHORIZATION, "Bearer " + tokenWithUserAuthorities())
		).andExpect(status().isNotFound());
	}

	@Test
	@DirtiesContext
	void testSolvePuzzleAuthenticatedShouldReturn200() throws Exception {
		userService.createUser(new UserDto("user123", "password", "user@email.com"));
		userService.createUser(new UserDto("user321", "password", "user@email.com"));

		int initialRating = userService.getUserByUsername("user321").getPuzzleRating();
		PuzzleDto puzzle = puzzleService.createPuzzle(new PuzzleDto("fen", List.of("e2e3"), 1000, List.of("Theme1")));
		PuzzleSolutionDto puzzleSolutionDto = new PuzzleSolutionDto(puzzle.getId(), 0, puzzle.getMoves());

		mockMvc.perform(
						post("/puzzles/solve/")
								.header(AUTHORIZATION, "Bearer " + tokenWithUserAuthorities())
								.contentType(APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(puzzleSolutionDto))
				).andExpect(status().isOk())
				.andExpect(jsonPath("$.delta").value(greaterThan(0)));

		Integer newRating = userService.getUserByUsername("user321").getPuzzleRating();

		assertThat(newRating).isGreaterThan(initialRating);
	}

	@Test
	void testGetAllPuzzlesNotAuthorizedShouldReturn403() throws Exception {
		mockMvc.perform(
				get("/puzzles/")
						.header(AUTHORIZATION, "Bearer " + tokenWithUserAuthorities())
		).andExpect(status().is(FORBIDDEN.value()));
	}

	@Test
	void testGetAllPuzzlesAuthorizedShouldReturnOk() throws Exception {
		mockMvc.perform(
						get("/puzzles/")
								.header(AUTHORIZATION, "Bearer " + tokenWithAdminAuthority())
				).andExpect(status().isOk())
				.andExpect(jsonPath("$.puzzles").isArray());
	}

	@Test
	void testGetPuzzleByIdNotAuthorizedShouldReturn403() throws Exception {
		mockMvc.perform(
				get("/puzzles/12/")
						.header(AUTHORIZATION, "Bearer " + tokenWithUserAuthorities())
		).andExpect(status().is(FORBIDDEN.value()));
	}

	@Test
	void testGetPuzzleByIdAuthorizedShouldReturn404() throws Exception {
		mockMvc.perform(
				get("/puzzles/12/")
						.header(AUTHORIZATION, "Bearer " + tokenWithAdminAuthority())
		).andExpect(status().isNotFound());
	}

	@Test
	@DirtiesContext
	void testGetPuzzleByIdAuthorizedShouldReturn200WithPuzzle() throws Exception {
		PuzzleDto expected = puzzleService.createPuzzle(new PuzzleDto("fen", List.of("e2e3"), 1000, List.of("Theme1")));

		String responseBody = mockMvc.perform(
						get("/puzzles/{id}/", expected.getId())
								.header(AUTHORIZATION, "Bearer " + tokenWithAdminAuthority())
				).andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		assertThat(objectMapper.readValue(responseBody, PuzzleDto.class)).isEqualTo(expected);
	}

	@Test
	void testPostPuzzleNotAuthorizedShouldReturn403() throws Exception {
		String content = objectMapper.writeValueAsString(new PuzzleDto());
		mockMvc.perform(
				post("/puzzles/")
						.contentType(APPLICATION_JSON)
						.header(AUTHORIZATION, "Bearer " + tokenWithUserAuthorities())
						.content(content)
		).andExpect(status().is(FORBIDDEN.value()));
	}

	@Test
	@DirtiesContext
	void testPostPuzzleAuthorizedShouldReturn201() throws Exception {
		String content = objectMapper.writeValueAsString(new PuzzleDto("fen", List.of("e2e3"), 1000, List.of("Theme1")));

		mockMvc.perform(
				post("/puzzles/")
						.contentType(APPLICATION_JSON)
						.header(AUTHORIZATION, "Bearer " + tokenWithAdminAuthority())
						.content(content)
		).andExpect(status().is(CREATED.value()));

		assertThat(puzzleRepository.count()).isOne();
	}

	@Test
	void testDeletePuzzleNotAuthorizedShouldReturn403() throws Exception {
		mockMvc.perform(
				delete("/puzzles/12/")
						.header(AUTHORIZATION, "Bearer " + tokenWithUserAuthorities())
		).andExpect(status().is(FORBIDDEN.value()));
	}

	@Test
	@DirtiesContext
	void testDeletePuzzleAuthorizedShouldReturn203() throws Exception {
		PuzzleDto puzzle = puzzleService.createPuzzle(new PuzzleDto("fen", List.of("e2e3"), 1000, List.of("Theme1")));

		mockMvc.perform(
				delete("/puzzles/{id}/", puzzle.getId())
						.contentType(APPLICATION_JSON)
						.header(AUTHORIZATION, "Bearer " + tokenWithAdminAuthority())
		).andExpect(status().is(NO_CONTENT.value()));

		assertThat(puzzleRepository.count()).isZero();
	}

	private String tokenWithUserAuthorities() {
		return jwtUtil.generateAccessToken(new UserDetailsImpl("user321", "", List.of(new SimpleGrantedAuthority(Role.USER.toString()))));
	}

	private String tokenWithAdminAuthority() {
		List<GrantedAuthority> authorities = Arrays.stream(Role.values())
				.map(Role::toString)
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		return jwtUtil.generateAccessToken(new UserDetailsImpl("user123", "", authorities));
	}
}