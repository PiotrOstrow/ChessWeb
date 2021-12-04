package com.github.piotrostrow.chess.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class JwtTokenUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

	private static final String ROLE_KEY = "roles";

	private final String jwtSecret = "secret123";

	public String generateAccessToken(UserDetails user) {
		String roles = "";

		return Jwts.builder()
				.setSubject(user.getUsername())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
				.claim(ROLE_KEY, roles)
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}

	public Optional<Authentication> getAuthentication(String token) {
		try {
			Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();

			// TODO: implement roles
			List<GrantedAuthority> authorities = Collections.emptyList();

			UserDetails principal = new UserDetailsImpl(claims.getSubject(), "");

			return Optional.of(new UsernamePasswordAuthenticationToken(principal, token, authorities));
		} catch (JwtException e) {
			LOGGER.error("Error parsing JWT - {}", e.getMessage());
			return Optional.empty();
		}
	}
}