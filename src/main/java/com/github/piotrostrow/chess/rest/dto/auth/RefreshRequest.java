package com.github.piotrostrow.chess.rest.dto.auth;

import java.util.Objects;

public class RefreshRequest {

	private String refreshToken;

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RefreshRequest that = (RefreshRequest) o;
		return Objects.equals(refreshToken, that.refreshToken);
	}

	@Override
	public int hashCode() {
		return Objects.hash(refreshToken);
	}

	@Override
	public String toString() {
		return "RefreshRequest{" +
				"refreshToken='" + refreshToken + '\'' +
				'}';
	}
}
