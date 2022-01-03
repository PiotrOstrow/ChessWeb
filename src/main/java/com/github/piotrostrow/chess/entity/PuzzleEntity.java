package com.github.piotrostrow.chess.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class PuzzleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String fen;
	private String moves;
	private int rating;

	@ManyToMany(fetch = FetchType.EAGER)
	private Set<PuzzleThemeEntity> themes = new HashSet<>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFen() {
		return fen;
	}

	public void setFen(String fen) {
		this.fen = fen;
	}

	public String getMoves() {
		return moves;
	}

	public void setMoves(String moves) {
		this.moves = moves;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public Set<PuzzleThemeEntity> getThemes() {
		return themes;
	}

	public void setThemes(Set<PuzzleThemeEntity> themes) {
		this.themes = themes;
	}
}
