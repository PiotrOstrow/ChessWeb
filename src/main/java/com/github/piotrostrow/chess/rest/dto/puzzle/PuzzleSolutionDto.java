package com.github.piotrostrow.chess.rest.dto.puzzle;

import java.util.List;

public class PuzzleSolutionDto {

	private long id;

	private int time;

	private List<String> moves;

	public PuzzleSolutionDto() {

	}

	public PuzzleSolutionDto(long id, int time, List<String> moves) {
		this.id = id;
		this.time = time;
		this.moves = moves;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public List<String> getMoves() {
		return moves;
	}

	public void setMoves(List<String> moves) {
		this.moves = moves;
	}
}
