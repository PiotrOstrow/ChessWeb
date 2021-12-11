package com.github.piotrostrow.chess.domain.chess.pieces;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.Position;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class KnightTest {

	@Test
	void testKnightMiddleOfTheBoard() {
		Knight knight = new Knight(Color.BLACK, new Position(4, 4));

		Collection<Position> actual = new HashSet<>(knight.getPseudoLegalMoves(Collections.emptyMap()));

		assertThat(actual).hasSize(8);
	}

	@Test
	void testKnightInCorner() {
		Knight knight = new Knight(Color.BLACK, new Position(0, 0));

		Collection<Position> actual = new HashSet<>(knight.getPseudoLegalMoves(Collections.emptyMap()));

		assertThat(actual).hasSize(2);
	}

	@Test
	void testKnightMoveSquareOccupiedByOwnPiece() {
		Knight knight = new Knight(Color.BLACK, new Position(0, 0));

		Piece piece = new Bishop(Color.BLACK, new Position(2, 1));
		Set<Position> actual = new HashSet<>(knight.getPseudoLegalMoves(Map.of(piece.getPosition(), piece)));

		assertThat(actual).hasSize(1).doesNotContain(piece.getPosition());
	}

	@Test
	void testKnightMoveSquareOccupiedByOpponent() {
		Knight knight = new Knight(Color.BLACK, new Position(0, 0));

		Piece piece = new Bishop(Color.WHITE, new Position(2, 1));
		Set<Position> actual = new HashSet<>(knight.getPseudoLegalMoves(Map.of(piece.getPosition(), piece)));

		assertThat(actual).hasSize(2).contains(piece.getPosition());
	}
}