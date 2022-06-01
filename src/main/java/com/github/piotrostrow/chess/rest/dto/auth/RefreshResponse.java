package com.github.piotrostrow.chess.rest.dto.auth;

import java.util.Objects;

public class RefreshResponse {

	private final String accessToken;

	public RefreshResponse(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RefreshResponse that = (RefreshResponse) o;
		return Objects.equals(accessToken, that.accessToken);
	}

	@Override
	public int hashCode() {
		return Objects.hash(accessToken);
	}

	@Override
	public String toString() {
		return "RefreshResponse{" +
				"accessToken='" + accessToken + '\'' +
				'}';
	}
}
