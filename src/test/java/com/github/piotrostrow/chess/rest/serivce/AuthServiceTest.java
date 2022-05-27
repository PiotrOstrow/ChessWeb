package com.github.piotrostrow.chess.rest.serivce;

import com.github.piotrostrow.chess.rest.dto.auth.AuthRequest;
import com.github.piotrostrow.chess.rest.dto.auth.AuthResponse;
import com.github.piotrostrow.chess.security.UserDetailsImpl;
import com.github.piotrostrow.chess.security.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {

	private static final String LOGIN = "username123";
	private static final String PASSWORD = "qwerty";
	private static final String ACCESS_TOKEN = "accessToken123";
	private static final String REFRESH_TOKEN = "refreshToken123";
	private static final Authentication authentication = new UsernamePasswordAuthenticationToken(new UserDetailsImpl(LOGIN, PASSWORD), null);

	private AuthenticationManager authenticationManager;
	private JwtUtil jwtUtil;

	@BeforeEach
	void init() {
		jwtUtil = mock(JwtUtil.class);
		when(jwtUtil.generateAccessToken(any())).thenReturn(ACCESS_TOKEN);
		when(jwtUtil.generateRefreshToken(any(), any())).thenReturn(REFRESH_TOKEN);

		authenticationManager = mock(AuthenticationManager.class);
	}

	@Test
	void testAuthenticateFailureThrowsException() {
		when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);
		AuthService authService = new AuthService(authenticationManager, jwtUtil);

		AuthRequest authRequest = new AuthRequest(LOGIN, PASSWORD);
		assertThatThrownBy(() -> authService.authenticate(authRequest)).isInstanceOf(AuthenticationException.class);
	}

	@Test
	void testAuthenticateSuccess() {
		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		AuthService authService = new AuthService(authenticationManager, jwtUtil);

		AuthResponse authResponse = authService.authenticate(new AuthRequest(LOGIN, PASSWORD));

		assertThat(authResponse).isEqualTo(new AuthResponse(LOGIN, Collections.emptyList(), ACCESS_TOKEN, REFRESH_TOKEN));
	}
}