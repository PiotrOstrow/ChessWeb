package com.github.piotrostrow.chess.controller;

import com.github.piotrostrow.chess.dto.AuthRequest;
import com.github.piotrostrow.chess.security.JwtTokenUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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

	public AuthController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenUtil = jwtTokenUtil;
	}

	@PostMapping("login")
	public ResponseEntity<Object> login(@RequestBody @Valid AuthRequest request) {
		try {
			Authentication authenticate = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
			);

			UserDetails userDetails = (UserDetails) authenticate.getPrincipal();

			return ResponseEntity.ok()
					.header(HttpHeaders.AUTHORIZATION, jwtTokenUtil.generateAccessToken(userDetails))
					.body(userDetails);
		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
}
