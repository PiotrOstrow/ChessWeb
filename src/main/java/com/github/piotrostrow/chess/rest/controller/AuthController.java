package com.github.piotrostrow.chess.rest.controller;

import com.github.piotrostrow.chess.rest.dto.AuthRequest;
import com.github.piotrostrow.chess.rest.dto.AuthResponse;
import com.github.piotrostrow.chess.security.JwtTokenUtil;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
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
	private final JwtTokenUtil jwtTokenUtil;
	private final ModelMapper modelMapper;

	// TODO: service
	public AuthController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, ModelMapper modelMapper) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenUtil = jwtTokenUtil;
		this.modelMapper = modelMapper;
	}

	@PostMapping("login")
	public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
		Authentication authenticate = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
		);

		UserDetails userDetails = (UserDetails) authenticate.getPrincipal();
		AuthResponse authResponse = modelMapper.map(userDetails, AuthResponse.class);

		return ResponseEntity.ok()
				.header(HttpHeaders.AUTHORIZATION, jwtTokenUtil.generateAccessToken(userDetails))
				.body(authResponse);
	}
}
