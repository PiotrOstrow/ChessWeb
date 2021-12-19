package com.github.piotrostrow.chess.rest.controller;

import com.github.piotrostrow.chess.rest.dto.AuthRequest;
import com.github.piotrostrow.chess.rest.dto.AuthResponse;
import com.github.piotrostrow.chess.security.JwtTokenUtil;
import com.github.piotrostrow.chess.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ModelMapper.class})
class AuthControllerTest {

	private static final String LOGIN = "username123";
	private static final String PASSWORD = "qwerty";
	private static final String TOKEN = "token";
	private static final Authentication authentication = new UsernamePasswordAuthenticationToken(new UserDetailsImpl(LOGIN, PASSWORD), null);

	private JwtTokenUtil jwtTokenUtil;
	private AuthenticationManager authenticationManager;

	@Autowired
	private ModelMapper modelMapper;

	@BeforeAll
	void init() {
		jwtTokenUtil = mock(JwtTokenUtil.class);
		when(jwtTokenUtil.generateAccessToken(any())).thenReturn(TOKEN);

		authenticationManager = mock(AuthenticationManager.class);
	}

	@Test
	void testLoginFailure() {
		when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);
		AuthController authController = new AuthController(authenticationManager, jwtTokenUtil, modelMapper);

		ResponseEntity<AuthResponse> responseEntity = authController.login(new AuthRequest(LOGIN, PASSWORD));
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void testLoginSuccess() {
		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		AuthController authController = new AuthController(authenticationManager, jwtTokenUtil, modelMapper);

		ResponseEntity<AuthResponse> responseEntity = authController.login(new AuthRequest(LOGIN, PASSWORD));

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getHeaders()).containsKey(HttpHeaders.AUTHORIZATION);
		assertThat(responseEntity.getHeaders().get(HttpHeaders.AUTHORIZATION)).hasSize(1).first().isEqualTo(TOKEN);
	}
}