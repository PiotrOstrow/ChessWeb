package com.github.piotrostrow.chess.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class RefreshTokenEntity {

	@Id
	@GeneratedValue
	private UUID id;

	// should be the entity or id at least, but I'm tired of JPA...
	private String username;

	public RefreshTokenEntity() {

	}

	public RefreshTokenEntity(String username) {
		this.username = username;
	}

	public RefreshTokenEntity(UUID id, String username) {
		this.id = id;
		this.username = username;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
