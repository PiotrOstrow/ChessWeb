package com.github.piotrostrow.chess.domain.chess.pieces;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.Position;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class RookTest {

	@Test
	void testRooksInCorners() {
		Rook rook = new Rook(Color.WHITE, new Position(0, 0));
		Rook rook2 = new Rook(Color.WHITE, new Position(7, 7));

		Collection<Position> actual = new HashSet<>(rook.getPseudoLegalMoves(Collections.emptyMap()));
		Collection<Position> actual2 = new HashSet<>(rook2.getPseudoLegalMoves(Collections.emptyMap()));

		assertThat(actual).hasSize(14);
		assertThat(actual2).hasSize(14);
	}

	@Test
	void testRookInTheMiddle() {
		Rook rook = new Rook(Color.WHITE, new Position(4, 3));

		Collection<Position> actual = rook.getPseudoLegalMoves(Collections.emptyMap()).stream()
				.filter(e -> e.getX() == rook.getPosition().getX() || e.getY() == rook.getPosition().getY())
				.filter(e -> e.getX() != rook.getPosition().getX() || e.getY() != rook.getPosition().getY())
				.collect(Collectors.toSet());

		assertThat(actual).hasSize(14);
	}

	@Test
	void testRookBlockedByOwnPiece() {
		Rook rook = new Rook(Color.WHITE, new Position(0, 0));

		Piece piece = new Bishop(Color.WHITE, new Position(0, 7));
		Set<Position> actual = new HashSet<>(rook.getPseudoLegalMoves(Map.of(piece.getPosition(), piece)));

		assertThat(actual).isNotEmpty().doesNotContain(piece.getPosition());
	}

	@Test
	void testRookNotBlockedByOpponentsPiece() {
		Rook rook = new Rook(Color.WHITE, new Position(0, 0));

		Piece piece = new Bishop(Color.BLACK, new Position(0, 5));
		Set<Position> actual = new HashSet<>(rook.getPseudoLegalMoves(Map.of(piece.getPosition(), piece)));

		assertThat(actual).isNotEmpty().contains(piece.getPosition());
	}

	@Test
	void testRookCutOffByOwnPieces() {
		Rook rook = new Rook(Color.WHITE, new Position(0, 0));

		Piece piece = new Bishop(Color.WHITE, new Position(0, 1));
		Piece piece2 = new Bishop(Color.WHITE, new Position(1, 0));
		Map<Position, Piece> pieces = Map.of(piece.getPosition(), piece, piece2.getPosition(), piece2);
		Set<Position> actual = new HashSet<>(rook.getPseudoLegalMoves(pieces));

		assertThat(actual).isEmpty();
	}

	@Test
	void testRookCutOffByOpponentsPieces() {
		Rook rook = new Rook(Color.WHITE, new Position(0, 0));

		Piece piece = new Bishop(Color.BLACK, new Position(0, 1));
		Piece piece2 = new Bishop(Color.BLACK, new Position(1, 0));
		Map<Position, Piece> pieces = Map.of(piece.getPosition(), piece, piece2.getPosition(), piece2);
		Set<Position> actual = new HashSet<>(rook.getPseudoLegalMoves(pieces));

		assertThat(actual)
				.hasSize(2)
				.contains(piece.getPosition())
				.contains(piece2.getPosition());
	}
}