package com.github.piotrostrow.chess.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenUtilTest {

	private static final String SECRET_KEY = "secret123";

	private final JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();

	@Test
	void testGetAuthenticationWithValidToken() {
		String token = Jwts.builder()
				.setSubject("John")
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
				.signWith(SignatureAlgorithm.HS512, SECRET_KEY)
				.compact();

		Optional<Authentication> actual = jwtTokenUtil.getAuthentication(token);

		assertThat(actual).isPresent();
		assertThat(actual.get().getName()).isEqualTo("John");
	}

	@Test
	void testGetAuthenticationWithInvalidSignature() {
		String token = Jwts.builder()
				.setSubject("John")
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
				.signWith(SignatureAlgorithm.HS512, "secret321")
				.compact();

		Optional<Authentication> actual = jwtTokenUtil.getAuthentication(token);

		assertThat(actual).isEmpty();
	}

	@Test
	void testGetAuthenticationWithExpiredToken() {
		String token = Jwts.builder()
				.setSubject("John")
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000))
				.signWith(SignatureAlgorithm.HS512, "secret321")
				.compact();

		Optional<Authentication> actual = jwtTokenUtil.getAuthentication(token);

		assertThat(actual).isEmpty();
	}
}