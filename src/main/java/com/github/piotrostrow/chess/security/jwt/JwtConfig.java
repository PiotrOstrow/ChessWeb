package com.github.piotrostrow.chess.security.jwt;

public interface JwtConfig {

	String getAccessTokenSecret();

	long getAccessTokenLifetime();

	String getRefreshTokenSecret();

	long getRefreshTokenLifetime();
}
