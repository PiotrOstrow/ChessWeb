package com.github.piotrostrow.chess.rest.controller;

import com.github.piotrostrow.chess.rest.dto.auth.*;
import com.github.piotrostrow.chess.rest.serivce.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthController {

	private static final String REFRESH_TOKEN_COOKIE = "RefreshToken";

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("login")
	public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request, HttpServletResponse response) {
		AuthResult authResult = authService.authenticate(request);

		Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, authResult.getRefreshToken());
		cookie.setPath("/");
		response.addCookie(cookie);

		return ResponseEntity.ok(authResult.getAuthResponse());
	}

	@PostMapping("refresh")
	public ResponseEntity<Object> refresh(HttpServletRequest request, HttpServletResponse response) {
		Cookie cookie = WebUtils.getCookie(request, REFRESH_TOKEN_COOKIE);
		if (cookie == null)
			throw new BadCredentialsException("No refresh token present");

		RefreshResult refreshResult = authService.refreshAccessToken(cookie.getValue());

		Cookie newCookie = new Cookie(REFRESH_TOKEN_COOKIE, refreshResult.getNewRefreshToken());
		newCookie.setPath("/");
		response.addCookie(newCookie);

		return ResponseEntity.ok(new RefreshResponse(refreshResult.getNewAccessToken()));
	}

	@PostMapping("logout")
	public ResponseEntity<Object> logout(HttpServletRequest request) {
		Cookie cookie = WebUtils.getCookie(request, REFRESH_TOKEN_COOKIE);
		if (cookie == null)
			throw new BadCredentialsException("No refresh token present");

		authService.invalidateRefreshToken(cookie.getValue());
		return ResponseEntity.noContent().build();
	}
}
