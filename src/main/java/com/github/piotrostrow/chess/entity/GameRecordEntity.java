package com.github.piotrostrow.chess.entity;

import com.github.piotrostrow.chess.domain.chess.Color;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class GameRecordEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@NotNull
	private UserEntity user;

	@OneToOne
	@NotNull
	private PGNEntity pgn;

	@NotNull
	private Color color;

	// TODO: add date

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

	public PGNEntity getPgn() {
		return pgn;
	}

	public void setPgn(PGNEntity pgn) {
		this.pgn = pgn;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
