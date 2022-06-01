package com.github.piotrostrow.chess.rest.serivce;

import com.github.piotrostrow.chess.entity.RefreshTokenEntity;
import com.github.piotrostrow.chess.repository.RefreshTokenRepository;
import com.github.piotrostrow.chess.rest.dto.auth.AuthRequest;
import com.github.piotrostrow.chess.rest.dto.auth.AuthResponse;
import com.github.piotrostrow.chess.rest.dto.auth.AuthResult;
import com.github.piotrostrow.chess.rest.dto.auth.RefreshResult;
import com.github.piotrostrow.chess.security.UserDetailsImpl;
import com.github.piotrostrow.chess.security.jwt.JwtUtil;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

	private static final String LOGIN = "username123";
	private static final String PASSWORD = "qwerty";
	private static final String ACCESS_TOKEN = "accessToken123";
	private static final String REFRESH_TOKEN = "refreshToken123";

	private static final Authentication authentication = new UsernamePasswordAuthenticationToken(new UserDetailsImpl(LOGIN, PASSWORD), null);

	private static final UUID REFRESH_TOKEN_UUID = UUID.randomUUID();

	private AuthenticationManager authenticationManager;
	private JwtUtil jwtUtil;
	private RefreshTokenRepository refreshTokenRepository;
	private UserDetailsService userDetailsService;

	private AuthService authService;

	@BeforeEach
	void init() {
		authenticationManager = mock(AuthenticationManager.class);
		jwtUtil = mock(JwtUtil.class);
		refreshTokenRepository = mock(RefreshTokenRepository.class);
		userDetailsService = mock(UserDetailsService.class);

		when(jwtUtil.generateAccessToken(any())).thenReturn(ACCESS_TOKEN);
		when(jwtUtil.generateRefreshToken(any(), any())).thenReturn(REFRESH_TOKEN);
		when(jwtUtil.getRefreshTokenClaims(REFRESH_TOKEN)).thenReturn(new DefaultClaims(Map.of("jti", REFRESH_TOKEN_UUID.toString(), "sub", LOGIN)));

		when(refreshTokenRepository.save(any())).thenReturn(new RefreshTokenEntity(REFRESH_TOKEN_UUID, LOGIN));
		when(refreshTokenRepository.findById(UUID.fromString(REFRESH_TOKEN_UUID.toString()))).thenReturn(Optional.of(new RefreshTokenEntity(REFRESH_TOKEN_UUID, LOGIN)));

		when(userDetailsService.loadUserByUsername(LOGIN)).thenReturn(new UserDetailsImpl(LOGIN, null));

		authService = new AuthService(authenticationManager, jwtUtil, refreshTokenRepository, userDetailsService);
	}

	@Test
	@SuppressWarnings("ConstantConditions")
	void testAuthenticateSuccess() {
		when(authenticationManager.authenticate(any())).thenReturn(authentication);

		AuthResult authResult = authService.authenticate(new AuthRequest(LOGIN, PASSWORD));

		assertThat(authResult).isEqualTo(new AuthResult(new AuthResponse(LOGIN, Collections.emptyList(), ACCESS_TOKEN), REFRESH_TOKEN));
		verify(refreshTokenRepository, times(1)).save(ArgumentMatchers.refEq(new RefreshTokenEntity(LOGIN)));
	}

	@Test
	void testAuthenticateFailureThrowsException() {
		when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);

		AuthRequest authRequest = new AuthRequest(LOGIN, PASSWORD);

		assertThatThrownBy(() -> authService.authenticate(authRequest)).isInstanceOf(AuthenticationException.class);
	}

	@Test
	void testRefreshTokenSuccess() {
		String newAccessToken = "token222";
		String newRefreshToken = "token555";

		when(jwtUtil.generateAccessToken(any())).thenReturn(newAccessToken);
		when(jwtUtil.generateRefreshToken(LOGIN, REFRESH_TOKEN_UUID.toString())).thenReturn(newRefreshToken);

		RefreshResult refreshResult = authService.refreshAccessToken(REFRESH_TOKEN);

		assertThat(refreshResult).isEqualTo(new RefreshResult(newAccessToken, newRefreshToken));
	}

	@Test
	void testRefreshTokenInvalidToken() {
		when(jwtUtil.getRefreshTokenClaims(any())).thenReturn(null);

		assertThatThrownBy(() -> authService.refreshAccessToken(REFRESH_TOKEN))
				.isInstanceOf(BadCredentialsException.class)
				.hasMessage("Invalid refresh token");
	}

	@Test
	void testRefreshTokenInvalidated() {
		when(refreshTokenRepository.findById(any())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> authService.refreshAccessToken(REFRESH_TOKEN))
				.isInstanceOf(BadCredentialsException.class)
				.hasMessage("Refresh token has been invalidated");
	}

	@Test
	void testRefreshTokenUserNoLongerExists() {
		when(userDetailsService.loadUserByUsername(any())).thenThrow(new UsernameNotFoundException(LOGIN + " not found"));

		assertThatThrownBy(() -> authService.refreshAccessToken(REFRESH_TOKEN))
				.isInstanceOf(BadCredentialsException.class)
				.hasMessage("User not found for the given token");
	}
}