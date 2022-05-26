package com.github.piotrostrow.chess.security.jwt;

import org.springframework.stereotype.Component;

@Component
public class JwtConfigDevImpl implements JwtConfig {

	@Override
	public String getAccessTokenSecret() {
		return "secret123";
	}

	@Override
	public long getAccessTokenLifetime() {
		return 7 * 24 * 60 * 60 * 1000L;
	}
}
