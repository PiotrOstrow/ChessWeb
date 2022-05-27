package com.github.piotrostrow.chess.rest.serivce;

import com.github.piotrostrow.chess.rest.dto.auth.AuthRequest;
import com.github.piotrostrow.chess.rest.dto.auth.AuthResponse;
import com.github.piotrostrow.chess.rest.dto.auth.RefreshRequest;
import com.github.piotrostrow.chess.rest.dto.auth.RefreshResponse;
import com.github.piotrostrow.chess.security.jwt.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;

	public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}

	public AuthResponse authenticate(AuthRequest request) {
		Authentication authenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
		Authentication authentication = authenticationManager.authenticate(authenticationToken);

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();

		String accessToken = jwtUtil.generateAccessToken(userDetails);
		String refreshToken = generateRefreshToken(userDetails);

		return new AuthResponse(userDetails.getUsername(), userDetails.getAuthorities(), accessToken, refreshToken);
	}

	public RefreshResponse refreshAccessToken(RefreshRequest request) {
		return new RefreshResponse();
	}

	private String generateRefreshToken(UserDetails userDetails) {
		String id = UUID.randomUUID().toString();
		return jwtUtil.generateRefreshToken(userDetails, id);
	}
}
