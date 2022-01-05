package com.github.piotrostrow.chess.rest.dto;

public class PuzzleSolutionResponse {

	private boolean correct;

	private int rating;

	private int delta;

	public PuzzleSolutionResponse(boolean correct, int rating, int delta) {
		this.correct = correct;
		this.rating = rating;
		this.delta = delta;
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public int getDelta() {
		return delta;
	}

	public void setDelta(int delta) {
		this.delta = delta;
	}
}
