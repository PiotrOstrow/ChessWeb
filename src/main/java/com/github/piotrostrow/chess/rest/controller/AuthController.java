package com.github.piotrostrow.chess.rest.controller;

import com.github.piotrostrow.chess.rest.dto.auth.AuthRequest;
import com.github.piotrostrow.chess.rest.dto.auth.AuthResponse;
import com.github.piotrostrow.chess.rest.dto.auth.RefreshRequest;
import com.github.piotrostrow.chess.rest.dto.auth.RefreshResponse;
import com.github.piotrostrow.chess.rest.serivce.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("login")
	public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
		AuthResponse authResponse = authService.authenticate(request);
		return ResponseEntity.ok(authResponse);
	}

	@PostMapping("refresh")
	public ResponseEntity<Object> refresh(@RequestBody @Valid RefreshRequest request) {
		RefreshResponse refreshResponse = authService.refreshAccessToken(request);
		return ResponseEntity.ok(refreshResponse);
	}
}
