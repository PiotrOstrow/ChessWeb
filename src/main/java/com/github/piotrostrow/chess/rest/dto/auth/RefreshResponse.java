package com.github.piotrostrow.chess.rest.dto.auth;

import java.util.Objects;

public class RefreshResponse {

	private final String accessToken;
	private final String refreshToken;

	public RefreshResponse(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RefreshResponse that = (RefreshResponse) o;
		return Objects.equals(accessToken, that.accessToken) && Objects.equals(refreshToken, that.refreshToken);
	}

	@Override
	public int hashCode() {
		return Objects.hash(accessToken, refreshToken);
	}

	@Override
	public String toString() {
		return "RefreshResponse{" +
				"accessToken='" + accessToken + '\'' +
				", refreshToken='" + refreshToken + '\'' +
				'}';
	}
}
