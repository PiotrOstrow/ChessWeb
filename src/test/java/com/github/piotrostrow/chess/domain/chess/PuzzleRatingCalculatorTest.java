package com.github.piotrostrow.chess.domain.chess;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PuzzleRatingCalculatorTest {

	@Test
	void testEqualRatingPuzzleSolvedShouldReturnHalfKValue() {
		int playerRating = 1200;
		int puzzleRating = 1200;

		int delta = PuzzleRatingCalculator.calculateDelta(playerRating, puzzleRating, true);

		assertThat(delta).isEqualTo((int) Math.round(PuzzleRatingCalculator.K / 2));
	}

	@Test
	void testEqualRatingPuzzleNotSolvedShouldReturnNegativeHalfKValue() {
		int playerRating = 1200;
		int puzzleRating = 1200;

		int delta = PuzzleRatingCalculator.calculateDelta(playerRating, puzzleRating, false);

		assertThat(delta).isEqualTo((int) -Math.round(PuzzleRatingCalculator.K / 2));
	}

	@Test
	void testPlayerAbovePuzzleRatingPuzzleSolvedShouldReturnLessThanHalfKValue() {
		int playerRating = 1200;
		int puzzleRating = 1100;

		int delta = PuzzleRatingCalculator.calculateDelta(playerRating, puzzleRating, true);

		assertThat(delta).isLessThan((int) Math.round(PuzzleRatingCalculator.K / 2));
	}

	@Test
	void testPlayerAbovePuzzleRatingPuzzleNotSolvedShouldReturnLessThanNegativeHalfKValue() {
		int playerRating = 1200;
		int puzzleRating = 1100;

		int delta = PuzzleRatingCalculator.calculateDelta(playerRating, puzzleRating, false);

		assertThat(delta).isLessThan((int) -Math.round(PuzzleRatingCalculator.K / 2));
	}

	@Test
	void testPlayerBellowPuzzleRatingPuzzleSolvedShouldReturnMoreThanHalfKValue() {
		int playerRating = 1200;
		int puzzleRating = 1300;

		int delta = PuzzleRatingCalculator.calculateDelta(playerRating, puzzleRating, true);

		assertThat(delta).isGreaterThan((int) Math.round(PuzzleRatingCalculator.K / 2));
	}

	@Test
	void testPlayerBellowPuzzleRatingPuzzleNotSolvedShouldReturnMoreThanNegativeHalfKValue() {
		int playerRating = 1200;
		int puzzleRating = 1300;

		int delta = PuzzleRatingCalculator.calculateDelta(playerRating, puzzleRating, false);

		assertThat(delta).isGreaterThan((int) -Math.round(PuzzleRatingCalculator.K / 2));
	}

	@Test
	void testPlayersRatingVeryHighDeltaShouldBeCloseTo0() {
		int playerRating = 1200;
		int puzzleRating = 600;

		int delta = PuzzleRatingCalculator.calculateDelta(playerRating, puzzleRating, true);

		assertThat(delta).isCloseTo(0, Offset.offset(1));
	}

	@Test
	void testPuzzleRatingVeryLowDeltaShouldBeCloseToKValue() {
		int playerRating = 1200;
		int puzzleRating = 2000;

		int delta = PuzzleRatingCalculator.calculateDelta(playerRating, puzzleRating, true);

		assertThat(delta).isCloseTo((int) PuzzleRatingCalculator.K, Offset.offset(1));
	}
}