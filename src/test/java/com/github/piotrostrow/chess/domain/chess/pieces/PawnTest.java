package com.github.piotrostrow.chess.domain.chess.pieces;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.Position;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PawnTest {

	@Test
	void testPawnStartPositionWhite() {
		Pawn pawn = new Pawn(Color.WHITE, new Position(0, 1));

		Collection<Position> actual = pawn.getPseudoLegalMoves(Collections.emptyMap());

		assertThat(actual)
				.hasSize(2)
				.contains(pawn.getPosition().plusY(1))
				.contains(pawn.getPosition().plusY(2));
	}

	@Test
	void testPawnStartPositionBlack() {
		Pawn pawn = new Pawn(Color.BLACK, new Position(0, 6));

		Collection<Position> actual = pawn.getPseudoLegalMoves(Collections.emptyMap());

		assertThat(actual)
				.hasSize(2)
				.contains(pawn.getPosition().plusY(-1))
				.contains(pawn.getPosition().plusY(-2));
	}

	@Test
	void testAdvancedPawnHasSingleMove() {
		Pawn pawn = new Pawn(Color.WHITE, new Position(0, 2));

		Collection<Position> actual = pawn.getPseudoLegalMoves(Collections.emptyMap());

		assertThat(actual)
				.hasSize(1)
				.contains(pawn.getPosition().plusY(1));
	}

	@Test
	void testAdvancedPawnBlockedByOpponent() {
		Pawn pawn = new Pawn(Color.WHITE, new Position(0, 2));

		Piece piece = new Bishop(Color.BLACK, pawn.getPosition().plusY(1));
		Collection<Position> actual = pawn.getPseudoLegalMoves(Map.of(piece.getPosition(), piece));

		assertThat(actual).isEmpty();
	}

	@Test
	void testAdvancedPawnBlockedByOwnPice() {
		Pawn pawn = new Pawn(Color.WHITE, new Position(0, 2));

		Piece piece = new Bishop(Color.WHITE, pawn.getPosition().plusY(1));
		Collection<Position> actual = pawn.getPseudoLegalMoves(Map.of(piece.getPosition(), piece));

		assertThat(actual).isEmpty();
	}

	@Test
	void testPawnBothCaptures() {
		Pawn pawn = new Pawn(Color.WHITE, new Position(1, 2));

		Piece piece = new Bishop(Color.BLACK, pawn.getPosition().plus(1, 1));
		Piece piece2 = new Pawn(Color.BLACK, pawn.getPosition().plus(-1, 1));
		Map<Position, Piece> pieces = Map.of(piece.getPosition(), piece, piece2.getPosition(), piece2);

		Collection<Position> actual = pawn.getPseudoLegalMoves(pieces);

		assertThat(actual)
				.hasSize(3)
				.contains(piece.getPosition())
				.contains(piece2.getPosition());
	}

	@Test
	void testWhitePawnInInitialPositionBlockedByOwnPawn() {
		Pawn pawn = new Pawn(Color.WHITE, new Position("e2"));
		Pawn blockingPawn = new Pawn(Color.WHITE, new Position("e3"));

		Map<Position, Piece> pieces = Map.of(pawn.getPosition(), pawn, blockingPawn.getPosition(), blockingPawn);

		Collection<Position> actual = pawn.getPseudoLegalMoves(pieces);
		assertThat(actual).isEmpty();
	}

	@Test
	void testBlackPawnInInitialPositionBlockedByOwnPawn() {
		Pawn pawn = new Pawn(Color.BLACK, new Position("e7"));
		Pawn blockingPawn = new Pawn(Color.WHITE, new Position("e6"));

		Map<Position, Piece> pieces = Map.of(pawn.getPosition(), pawn, blockingPawn.getPosition(), blockingPawn);

		Collection<Position> actual = pawn.getPseudoLegalMoves(pieces);
		assertThat(actual).isEmpty();
	}
}