package com.github.piotrostrow.chess.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class UserDto {

	private Long id;

	private String username;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	private String email;

	private int puzzleRating;

	public UserDto() {

	}

	public UserDto(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public UserDto(String username, String password, String email) {
		this.username = username;
		this.password = password;
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getPuzzleRating() {
		return puzzleRating;
	}

	public void setPuzzleRating(int puzzleRating) {
		this.puzzleRating = puzzleRating;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserDto userDto = (UserDto) o;
		return puzzleRating == userDto.puzzleRating && Objects.equals(id, userDto.id) && Objects.equals(username, userDto.username) && Objects.equals(password, userDto.password) && Objects.equals(email, userDto.email);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, username, password, email, puzzleRating);
	}
}
