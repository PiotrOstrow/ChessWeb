package com.github.piotrostrow.chess.domain.chess.pieces;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.Position;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KingTest {

	@Test
	void testKingMiddleOfTheBoard() {
		King king = new King(Color.BLACK, new Position(4, 3));

		Collection<Position> actual = king.getPseudoLegalMoves(Collections.emptyMap());

		assertThat(actual).hasSize(8);
	}

	@Test
	void testKingCorner() {
		King king = new King(Color.BLACK, new Position(0, 0));

		Collection<Position> actual = king.getPseudoLegalMoves(Collections.emptyMap());

		assertThat(actual).hasSize(3);
	}

	@Test
	void testKingEdge() {
		King king = new King(Color.BLACK, new Position(4, 0));

		Collection<Position> actual = king.getPseudoLegalMoves(Collections.emptyMap());

		assertThat(actual).hasSize(5);
	}

	@Test
	void testKingSquareOccupiedByOwnPiece() {
		King king = new King(Color.BLACK, new Position(4, 3));

		Piece piece = new Queen(Color.BLACK, new Position(4, 4));
		Collection<Position> actual = king.getPseudoLegalMoves(Map.of(piece.getPosition(), piece));

		assertThat(actual).hasSize(7);
	}

	@Test
	void testKingSquareOccupiedByOpponentsPiece() {
		King king = new King(Color.BLACK, new Position(4, 3));

		Piece piece = new Queen(Color.WHITE, new Position(4, 4));
		Collection<Position> actual = king.getPseudoLegalMoves(Map.of(piece.getPosition(), piece));

		assertThat(actual).hasSize(8);
	}
}