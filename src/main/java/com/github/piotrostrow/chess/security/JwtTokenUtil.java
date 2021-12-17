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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {

	// TODO refresh tokens
	// TODO secret storage

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

	private static final String ROLE_KEY = "roles";
	private static final String ROLE_DELIMITER = ", ";

	private static final String SECRET_KEY = "secret123";

	public String generateAccessToken(UserDetails user) {
		String roles = user.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(ROLE_DELIMITER));

		return Jwts.builder()
				.setSubject(user.getUsername())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
				.claim(ROLE_KEY, roles)
				.signWith(SignatureAlgorithm.HS512, SECRET_KEY)
				.compact();
	}

	public Optional<Authentication> getAuthentication(String token) {
		try {
			Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();

			List<GrantedAuthority> authorities = getGrantedAuthorities(claims);

			UserDetails principal = new UserDetailsImpl(claims.getSubject(), "", authorities);

			return Optional.of(new UsernamePasswordAuthenticationToken(principal, token, authorities));
		} catch (JwtException e) {
			LOGGER.error("Error parsing JWT - {}", e.getMessage());
			return Optional.empty();
		}
	}

	private List<GrantedAuthority> getGrantedAuthorities(Claims claims) {
		if (claims.containsKey(ROLE_KEY)) {
			return Arrays.stream(claims.get(ROLE_KEY, String.class).split(ROLE_DELIMITER))
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
}