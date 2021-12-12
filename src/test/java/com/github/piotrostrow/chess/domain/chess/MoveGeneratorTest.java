package com.github.piotrostrow.chess.domain.chess;

import com.github.piotrostrow.chess.domain.chess.pieces.Piece;
import com.github.piotrostrow.chess.domain.chess.pieces.Rook;
import com.github.piotrostrow.chess.ws.dto.Move;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class MoveGeneratorTest {

	@Test
	void testCantMoveKingIntoCheck() {
		Fen fen = new Fen("k7/r7/8/8/8/8/8/1K6 w - - 0 1");
		Map<Position, Set<Position>> legalMoves = getLegalMoves(fen);

		assertThat(isMoveLegal(new Move("b1", "a1"), legalMoves)).isFalse();
		assertThat(isMoveLegal(new Move("b1", "a2"), legalMoves)).isFalse();
		assertThat(isMoveLegal(new Move("b1", "b2"), legalMoves)).isTrue();
		assertThat(isMoveLegal(new Move("b1", "c2"), legalMoves)).isTrue();
		assertThat(isMoveLegal(new Move("b1", "c1"), legalMoves)).isTrue();
	}

	@Test
	void testCantMoveRookPinnedToFile() {
		Fen fen = new Fen("k7/r7/8/8/8/8/R7/K7 w - - 0 1");
		Map<Position, Set<Position>> legalMoves = getLegalMoves(fen);

		List<Position> legalRookMoves = fen.getPieces().stream()
				.filter(e -> e instanceof Rook)
				.map(Piece::getPosition)
				.map(legalMoves::get)
				.flatMap(Set::stream)
				.collect(Collectors.toList());

		assertThat(legalRookMoves).filteredOn(e -> e.getX() != 0).isEmpty();

		for (int i = 3; i <= 7; i++) {
			assertThat(isMoveLegal(new Move("a2", "a" + i), legalMoves)).isTrue();
		}

		for (int i = 2; i < 7; i++) {
			assertThat(isMoveLegal(new Move("a7", "a" + i), legalMoves)).isTrue();
		}
	}

	@Test
	void testCantMovePinnedPiece() {
		Fen fen = new Fen("k7/8/6b1/8/4R3/3K4/8/8 w - - 0 1");
		Map<Position, Set<Position>> legalMoves = getLegalMoves(fen);

		List<Position> legalRookMoves = fen.getPieces().stream()
				.filter(e -> e instanceof Rook)
				.flatMap(e -> legalMoves.get(e.getPosition()).stream())
				.collect(Collectors.toList());

		assertThat(legalRookMoves).isEmpty();
	}

	@Test
	void testKingInCheckOnlyValidMoveIsToCapture() {
		Fen fen = new Fen("1k6/8/8/8/8/8/1q6/K7 w - - 0 1");
		Map<Position, Set<Position>> legalMoves = getLegalMoves(fen);

		assertThat(isMoveLegal(new Move("a1", "a2"), legalMoves)).isFalse();
		assertThat(isMoveLegal(new Move("a1", "b1"), legalMoves)).isFalse();
		assertThat(isMoveLegal(new Move("a1", "b2"), legalMoves)).isTrue();
	}

	@Test
	void testKingInCheckOnlyOneValidMoveCantCapture() {
		Fen fen = new Fen("1k6/8/8/8/8/8/2qq4/1K6 w - - 0 1");
		Map<Position, Set<Position>> legalMoves = getLegalMoves(fen);

		assertThat(isMoveLegal(new Move("b1", "a1"), legalMoves)).isTrue();
		assertThat(isMoveLegal(new Move("b1", "a2"), legalMoves)).isFalse();
		assertThat(isMoveLegal(new Move("b1", "b2"), legalMoves)).isFalse();
		assertThat(isMoveLegal(new Move("b1", "c2"), legalMoves)).isFalse();
		assertThat(isMoveLegal(new Move("b1", "c1"), legalMoves)).isFalse();
	}

	@Test
	void testKingInCheckOnlyLegalMoveToCaptureWithAnotherPiece() {
		Fen fen = new Fen("1k5B/8/8/8/8/8/1q2q3/K7 w - - 0 1");
		Map<Position, Set<Position>> legalMoves = getLegalMoves(fen);

		List<Move> whitesLegalMoves = fen.getPieces().stream()
				.filter(e -> e.getColor() == Color.WHITE)
				.flatMap(e -> legalMoves.get(e.getPosition()).stream().map(to -> new Move(e.getPosition(), to)))
				.collect(Collectors.toList());

		assertThat(whitesLegalMoves)
				.hasSize(1)
				.contains(new Move("h8", "b2"));
	}

	@Test
	void testKingInDoubleCheckCannotCapture() {
		Fen fen = new Fen("1k6/8/8/8/3q4/4Q3/5n2/3K4 w - - 0 1");
		Map<Position, Set<Position>> legalMoves = getLegalMoves(fen);

		Piece whiteQueen = fen.getPieces().stream().filter(e -> e.getPosition().equals(new Position(4, 2))).findFirst().orElseThrow();

		assertThat(legalMoves.get(whiteQueen.getPosition())).isEmpty();
	}

	@Test
	void testCheckMate() {
		Fen fen = new Fen("1k6/8/5q2/8/8/8/1q6/1K6 w - - 0 1");
		Map<Position, Set<Position>> legalMoves = getLegalMoves(fen);

		List<Position> whitesLegalMoves = fen.getPieces().stream()
				.filter(e -> e.getColor() == Color.WHITE)
				.flatMap(e -> legalMoves.get(e.getPosition()).stream())
				.collect(Collectors.toList());

		assertThat(whitesLegalMoves).isEmpty();
	}

	private Map<Position, Set<Position>> getLegalMoves(Fen fen) {
		Map<Position, Piece> pieces = fen.getPieces().stream().collect(Collectors.toMap(Piece::getPosition, e -> e));
		return MoveGenerator.generateLegalMoves(pieces);
	}

	private boolean isMoveLegal(Move move, Map<Position, Set<Position>> legalMoves) {
		return legalMoves.get(move.getFrom()).contains(move.getTo());
	}
}