package com.github.piotrostrow.chess.security.jwt;

import com.github.piotrostrow.chess.security.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);

	private static final String ROLE_KEY = "roles";
	private static final String ROLE_DELIMITER = ", ";

	private final JwtConfig jwtConfig;

	@Autowired
	public JwtUtil(JwtConfig jwtConfig) {
		this.jwtConfig = jwtConfig;
	}

	public String generateAccessToken(UserDetails user) {
		String roles = user.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(ROLE_DELIMITER));

		return Jwts.builder()
				.setSubject(user.getUsername())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getAccessTokenLifetime()))
				.claim(ROLE_KEY, roles)
				.signWith(SignatureAlgorithm.HS512, jwtConfig.getAccessTokenSecret())
				.compact();
	}

	public Optional<Authentication> getAuthentication(String token) {
		try {
			Claims claims = Jwts.parser().setSigningKey(jwtConfig.getAccessTokenSecret()).parseClaimsJws(token).getBody();

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