package com.github.piotrostrow.chess.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public class AuthResponse {

	private String username;
	private List<String> roles;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public void setAuthorities(List<? extends GrantedAuthority> authorities) {
		this.roles = authorities.stream()
				.map(GrantedAuthority::toString)
				.collect(Collectors.toList());
	}
}
