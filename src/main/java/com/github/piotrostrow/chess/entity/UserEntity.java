package com.github.piotrostrow.chess.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(indexes = @Index(name = "name_index", columnList = "username"))
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true)
	@NotEmpty
	@Size(min = 2, max = 32)
	private String username;

	private String password;

	@Email
	@NotEmpty
	private String email;

	@ManyToMany(fetch = FetchType.EAGER)
	private Set<RoleEntity> roles;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private Set<GamePlayedEntity> gamesPlayed = new HashSet<>();

	private int puzzleRating = 700;

	public UserEntity() {

	}

	public UserEntity(String username) {
		this.username = username;
	}

	public UserEntity(String username, String email) {
		this.username = username;
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

	public void setGamesPlayed(Set<GamePlayedEntity> gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}

	public Set<GamePlayedEntity> getGamesPlayed() {
		return gamesPlayed;
	}

	public void addGamePlayed(GamePlayedEntity gamePlayedEntity) {
		gamesPlayed.add(gamePlayedEntity);
		gamePlayedEntity.setUser(this);
	}

	public Set<RoleEntity> getRoles() {
		return roles;
	}

	public void setRoles(Set<RoleEntity> roles) {
		this.roles = roles;
	}

	public int getPuzzleRating() {
		return puzzleRating;
	}

	public void setPuzzleRating(int puzzleRating) {
		this.puzzleRating = puzzleRating;
	}
}
