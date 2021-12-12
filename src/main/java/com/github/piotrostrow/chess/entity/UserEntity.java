package com.github.piotrostrow.chess.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(indexes = @Index(name = "name_index", columnList = "username"))
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true)
	private String username;

	private String password;

	@Email
	@NotEmpty
	private String email;

	//@OneToMany(fetch = FetchType.LAZY)
	//private Set<GameRecordEntity> gamesPlayed;

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

	//public void setGamesPlayed(Set<GameRecordEntity> gamesPlayed) {
	//	this.gamesPlayed = gamesPlayed;
	//}

	//public Set<GameRecordEntity> getGamesPlayed() {
	//	return gamesPlayed;
	//}
}
