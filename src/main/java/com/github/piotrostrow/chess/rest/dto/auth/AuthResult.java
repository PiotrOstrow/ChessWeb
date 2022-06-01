package com.github.piotrostrow.chess.rest.dto.auth;

import java.util.Objects;

public class AuthResult {

	private final AuthResponse authResponse;
	private final String refreshToken;

	public AuthResult(AuthResponse authResponse, String refreshToken) {
		this.authResponse = authResponse;
		this.refreshToken = refreshToken;
	}

	public AuthResponse getAuthResponse() {
		return authResponse;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AuthResult that = (AuthResult) o;
		return Objects.equals(authResponse, that.authResponse) && Objects.equals(refreshToken, that.refreshToken);
	}

	@Override
	public int hashCode() {
		return Objects.hash(authResponse, refreshToken);
	}

	@Override
	public String toString() {
		return "AuthResult{" +
				"authResponse=" + authResponse +
				", refreshToken='" + refreshToken + '\'' +
				'}';
	}
}
