package com.github.piotrostrow.chess.rest.dto;

import java.util.List;

public class PuzzleSolutionDto {

	private long id;

	private int time;

	private List<String> moves;

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
