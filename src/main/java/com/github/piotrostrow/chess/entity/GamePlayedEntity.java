package com.github.piotrostrow.chess.entity;

import com.github.piotrostrow.chess.domain.chess.Color;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class GamePlayedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@ManyToOne
	private UserEntity user;

	@NotNull
	@ManyToOne
	private GameEntity game;

	@NotNull
	private Color color;

	public GamePlayedEntity() {
	}

	public GamePlayedEntity(Long id, UserEntity user, GameEntity game, Color color) {
		this.id = id;
		this.user = user;
		this.game = game;
		this.color = color;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public GameEntity getGame() {
		return game;
	}

	public void setGame(GameEntity gameEntity) {
		this.game = gameEntity;
		gameEntity.addGameRecord(this);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
