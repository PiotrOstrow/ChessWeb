package com.github.piotrostrow.chess.rest.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public class AuthResponse {

	private final String username;
	private final List<String> roles;
	private final String accessToken;

	public AuthResponse(String username, Collection<? extends GrantedAuthority> authorities, String accessToken) {
		this.username = username;
		this.roles = authorities.stream().map(GrantedAuthority::toString).collect(Collectors.toList());
		this.accessToken = accessToken;
	}

	public String getUsername() {
		return username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public String getAccessToken() {
		return accessToken;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AuthResponse that = (AuthResponse) o;
		return Objects.equals(username, that.username) && Objects.equals(roles, that.roles) && Objects.equals(accessToken, that.accessToken);
	}

	@Override
	public int hashCode() {
		return Objects.hash(username, roles, accessToken);
	}

	@Override
	public String toString() {
		return "AuthResponse{" +
				"username='" + username + '\'' +
				", roles=" + roles +
				", accessToken='" + accessToken + '\'' +
				'}';
	}
}
