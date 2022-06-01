package com.github.piotrostrow.chess.rest.dto.auth;

import java.util.Objects;

public class RefreshResult {

	private final String newAccessToken;
	private final String newRefreshToken;

	public RefreshResult(String newAccessToken, String newRefreshToken) {
		this.newAccessToken = newAccessToken;
		this.newRefreshToken = newRefreshToken;
	}

	public String getNewAccessToken() {
		return newAccessToken;
	}

	public String getNewRefreshToken() {
		return newRefreshToken;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RefreshResult that = (RefreshResult) o;
		return Objects.equals(newAccessToken, that.newAccessToken) && Objects.equals(newRefreshToken, that.newRefreshToken);
	}

	@Override
	public int hashCode() {
		return Objects.hash(newAccessToken, newRefreshToken);
	}

	@Override
	public String toString() {
		return "RefreshResult{" +
				"newAccessToken='" + newAccessToken + '\'' +
				", newRefreshToken='" + newRefreshToken + '\'' +
				'}';
	}
}
