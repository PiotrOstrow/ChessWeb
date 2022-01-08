package com.github.piotrostrow.chess.domain.chess;

public class PuzzleRatingCalculator {

	static final double K = 32;

	private PuzzleRatingCalculator() {
	}

	public static int calculateDelta(int playerRating, int puzzleRating, boolean correct) {
		double r1 = Math.pow(10, playerRating / 400.0);
		double r2 = Math.pow(10, puzzleRating / 400.0);

		double e1 = r1 / (r1 + r2);

		double s1 = correct ? 1.0 : 0.0;

		return (int) Math.round(K * (s1 - e1));
	}
}
