package com.github.piotrostrow.chess.rest.controller;

import com.github.piotrostrow.chess.rest.dto.AuthRequest;
import com.github.piotrostrow.chess.rest.dto.AuthResponse;
import com.github.piotrostrow.chess.security.jwt.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;

	// TODO: service
	public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}

	@PostMapping("login")
	public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
		);

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String accessToken = jwtUtil.generateAccessToken(userDetails);
		AuthResponse authResponse = new AuthResponse(userDetails.getUsername(), userDetails.getAuthorities(), accessToken);

		return ResponseEntity.ok(authResponse);
	}
}
