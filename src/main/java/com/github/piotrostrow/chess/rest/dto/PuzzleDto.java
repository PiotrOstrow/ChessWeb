package com.github.piotrostrow.chess.rest.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class PuzzleDto {

	private long id;
	private String fen;
	private int rating;
	private List<String> moves;
	private Collection<String> themes;

	public PuzzleDto() {
		this.moves = new ArrayList<>();
		this.themes = new ArrayList<>();
	}

	public PuzzleDto(String fen, List<String> moves, int rating, Collection<String> themes) {
		this.fen = fen;
		this.moves = moves;
		this.rating = rating;
		this.themes = themes;
	}

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

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public List<String> getMoves() {
		return moves;
	}

	public void setMoves(List<String> moves) {
		this.moves = moves;
	}

	public Collection<String> getThemes() {
		return themes;
	}

	public void setThemes(Collection<String> themes) {
		this.themes = themes;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PuzzleDto puzzleDto = (PuzzleDto) o;
		return id == puzzleDto.id && rating == puzzleDto.rating && Objects.equals(fen, puzzleDto.fen) && Objects.equals(moves, puzzleDto.moves) && Objects.equals(themes, puzzleDto.themes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, fen, rating, moves, themes);
	}
}
