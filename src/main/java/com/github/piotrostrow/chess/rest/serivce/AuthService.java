package com.github.piotrostrow.chess.rest.serivce;

import com.github.piotrostrow.chess.entity.RefreshTokenEntity;
import com.github.piotrostrow.chess.repository.RefreshTokenRepository;
import com.github.piotrostrow.chess.rest.dto.auth.AuthRequest;
import com.github.piotrostrow.chess.rest.dto.auth.AuthResponse;
import com.github.piotrostrow.chess.rest.dto.auth.AuthResult;
import com.github.piotrostrow.chess.rest.dto.auth.RefreshResult;
import com.github.piotrostrow.chess.security.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserDetailsService userDetailsService;

	public AuthService(AuthenticationManager authenticationManager,
					   JwtUtil jwtUtil,
					   RefreshTokenRepository refreshTokenRepository,
					   UserDetailsService userDetailsService) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.refreshTokenRepository = refreshTokenRepository;
		this.userDetailsService = userDetailsService;
	}

	public AuthResult authenticate(AuthRequest request) {
		Authentication authenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
		Authentication authentication = authenticationManager.authenticate(authenticationToken);

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();

		String accessToken = jwtUtil.generateAccessToken(userDetails);
		String refreshToken = generateRefreshToken(userDetails);

		return new AuthResult(new AuthResponse(userDetails.getUsername(), userDetails.getAuthorities(), accessToken), refreshToken);
	}

	private String generateRefreshToken(UserDetails userDetails) {
		RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.save(new RefreshTokenEntity(userDetails.getUsername()));

		return jwtUtil.generateRefreshToken(userDetails.getUsername(), refreshTokenEntity.getId().toString());
	}

	public RefreshResult refreshAccessToken(String refreshToken) {
		Claims refreshTokenClaims = getRefreshTokenClaims(refreshToken);

		RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findById(UUID.fromString(refreshTokenClaims.getId()))
				.orElseThrow(() -> new BadCredentialsException("Refresh token has been invalidated"));
		refreshTokenRepository.delete(refreshTokenEntity);

		UserDetails userDetails = getUserDetails(refreshTokenClaims);
		final String newAccessToken = jwtUtil.generateAccessToken(userDetails);
		final String newRefreshToken = generateRefreshToken(userDetails);

		return new RefreshResult(newAccessToken, newRefreshToken);
	}

	private UserDetails getUserDetails(Claims tokenClaims) {
		try {
			return userDetailsService.loadUserByUsername(tokenClaims.getSubject());
		} catch (UsernameNotFoundException e) {
			throw new BadCredentialsException("User not found for the given token");
		}
	}

	private Claims getRefreshTokenClaims(String refreshToken) {
		Claims claims = jwtUtil.getRefreshTokenClaims(refreshToken);

		if (claims == null || claims.getId() == null)
			throw new BadCredentialsException("Invalid refresh token");

		return claims;
	}
}
