package com.github.piotrostrow.chess.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;
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

	@Test
	void testGenerateTokenWithAuthorities() {
		Role role = Role.USER;
		List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role.toString()));
		UserDetails userDetails = new UserDetailsImpl("username", "password", authorities);

		String token = jwtTokenUtil.generateAccessToken(userDetails);
		Optional<Authentication> actual = jwtTokenUtil.getAuthentication(token);

		assertThat(actual).isPresent();
		assertThat(actual.get().getAuthorities()).map(GrantedAuthority::getAuthority).contains(role.toString());
	}
}