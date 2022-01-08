package com.github.piotrostrow.chess.domain.chess;

import com.github.piotrostrow.chess.entity.PuzzleDetailsEntity;
import com.github.piotrostrow.chess.entity.PuzzleEntity;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PuzzleSolutionValidatorTest {

	@Test
	void testMovesEqualShouldReturnTrue() {
		PuzzleDetailsEntity puzzleDetails = new PuzzleDetailsEntity(Fen.DEFAULT_STARTING_POSITION.asString(), "e2e3 e7e6");
		PuzzleEntity puzzleEntity = new PuzzleEntity(1, puzzleDetails, Collections.emptySet(), 1300);

		List<String> moves = List.of(puzzleDetails.getMoves().split(" "));

		assertThat(PuzzleSolutionValidator.isCorrectSolution(puzzleEntity, moves)).isTrue();
	}

	@Test
	void testMovesNotEqualShouldReturnFalse() {
		PuzzleDetailsEntity puzzleDetails = new PuzzleDetailsEntity(Fen.DEFAULT_STARTING_POSITION.asString(), "e2e3 e7e6");
		PuzzleEntity puzzleEntity = new PuzzleEntity(1, puzzleDetails, Collections.emptySet(), 1300);

		List<String> moves = List.of("e2e3 e7e5");

		assertThat(PuzzleSolutionValidator.isCorrectSolution(puzzleEntity, moves)).isFalse();
	}

	@Test
	void testAlternativeLastMoveEndsInAMateShouldReturnTrue() {
		PuzzleDetailsEntity puzzleDetails = new PuzzleDetailsEntity("3Q4/6kp/6p1/3P1p2/6K1/6P1/5P2/7q w - - 0 36", "g4f4 h1e4 f4g5 h7h6");
		PuzzleEntity puzzleEntity = new PuzzleEntity(1, puzzleDetails, Collections.emptySet(), 1300);

		List<String> moves = List.of("g4f4", "h1e4", "f4g5", "e4g4");

		assertThat(PuzzleSolutionValidator.isCorrectSolution(puzzleEntity, moves)).isTrue();
	}

	@Test
	void testOpponentsMovesIncorrectEndsInCheckmateShouldReturnFalse() {
		PuzzleDetailsEntity puzzleDetails = new PuzzleDetailsEntity("3Q4/6kp/6p1/3P1p2/6K1/6P1/5P2/7q w - - 0 36", "g4f4 h1e4 f4g5 h7h6");
		PuzzleEntity puzzleEntity = new PuzzleEntity(1, puzzleDetails, Collections.emptySet(), 1300);

		List<String> moves = List.of("g4g5", "h1h6");

		assertThat(PuzzleSolutionValidator.isCorrectSolution(puzzleEntity, moves)).isFalse();
	}

	@Test
	void testAlternativeLastMoveDoesNotEndInCheckMateShouldReturnFalse() {
		PuzzleDetailsEntity puzzleDetails = new PuzzleDetailsEntity("3Q4/6kp/6p1/3P1p2/6K1/6P1/5P2/7q w - - 0 36", "g4f4 h1e4 f4g5 h7h6");
		PuzzleEntity puzzleEntity = new PuzzleEntity(1, puzzleDetails, Collections.emptySet(), 1300);

		List<String> moves = List.of("g4f4", "h1e4", "f4g5", "e4e3");

		assertThat(PuzzleSolutionValidator.isCorrectSolution(puzzleEntity, moves)).isFalse();
	}

	@Test
	void testAllButLastCorrectMoveEqualSubmittedMovesGameEndsInMateAfterMoreThanOneMoveShouldReturnFalse() {
		PuzzleDetailsEntity puzzleDetails = new PuzzleDetailsEntity("3Q4/6kp/6p1/3P1p2/6K1/6P1/5P2/7q w - - 0 36", "g4f4 h1e4 f4g5 h7h6");
		PuzzleEntity puzzleEntity = new PuzzleEntity(1, puzzleDetails, Collections.emptySet(), 1300);

		List<String> moves = List.of("g4f4", "h1e4", "f4g5", "e4d4", "d8c8", "d5g5");

		assertThat(PuzzleSolutionValidator.isCorrectSolution(puzzleEntity, moves)).isFalse();
	}
}