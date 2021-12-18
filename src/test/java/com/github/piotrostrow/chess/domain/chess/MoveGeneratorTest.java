package com.github.piotrostrow.chess.domain.chess;

import com.github.piotrostrow.chess.domain.User;
import com.github.piotrostrow.chess.domain.chess.pieces.Piece;
import com.github.piotrostrow.chess.domain.chess.pieces.Rook;
import com.github.piotrostrow.chess.ws.dto.Move;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.piotrostrow.chess.domain.chess.CastlingMove.*;
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

		assertThat(whitesLegalMoves).hasSize(1).contains(new Move("h8", "b2"));
	}

	@Test
	void testKingInDoubleCheckCannotCapture() {
		Fen fen = new Fen("1k6/8/8/8/3q4/4Q3/5n2/3K4 w - - 0 1");
		Map<Position, Set<Position>> legalMoves = getLegalMoves(fen);

		assertThat(legalMoves.get(new Position(4, 2))).isEmpty();
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

	@Test
	void testPawnDoesNotControlSquareAhead() {
		Fen fen = new Fen("r1b1kb1r/p1pp1ppp/1pn2n2/8/2q5/4Pp2/PPPP3P/1R2K2R w Kkq - 0 1");
		Map<Position, Set<Position>> legalMoves = getLegalMoves(fen);

		assertThat(legalMoves.get(new Position(4, 0))).contains(new Position(5, 1));
	}

	@Test
	void testWhiteCanCastleKingSide() {
		Fen fen = new Fen("r1bqkb1r/pppp1ppp/2n1pn2/8/2B5/4PN2/PPPP1PPP/RNBQK2R w KQkq - 0 1");
		Map<Position, Set<Position>> legalMoves = getLegalMoves(fen);

		assertThat(legalMoves.get(new Position(4, 0))).contains(new Position(6, 0));
		assertCastling(WHITE_KING_SIDE, true, fen);
	}

	@Test
	void testWhiteCantCastleKingSideNotAvailable() {
		Fen fen = new Fen("r1bqkb1r/pppp1ppp/2n1pn2/8/2B5/4PN2/PPPP1PPP/RNBQK2R w Qkq - 0 1");
		Map<Position, Set<Position>> legalMoves = getLegalMoves(fen);

		assertThat(legalMoves.get(new Position(4, 0))).isNotEmpty().doesNotContain(new Position(6, 0));

		assertCastling(WHITE_KING_SIDE, false, fen);
	}

	@Test
	void testWhiteCanCastleQueenSide() {
		Fen fen = new Fen("r1bqkb1r/p1pp1ppp/1pn1pn2/8/2B5/4PN2/PPPP1PPP/R3K1R1 w Qkq - 0 1");
		assertCastling(WHITE_QUEEN_SIDE, true, fen);
	}

	@Test
	void testWhiteCantCastleQueenSideNotAvailable() {
		Fen fen = new Fen("r1bqkb1r/p1pp1ppp/1pn1pn2/8/2B5/4PN2/PPPP1PPP/R3K1R1 w kq - 0 1");
		assertCastling(WHITE_QUEEN_SIDE, false, fen);
	}


	@Test
	void testBlackCanCastleKingSide() {
		Fen fen = new Fen("r1bqk2r/p1pp1ppp/1pnbpn2/8/2B5/4PN2/PPPP1PPP/RNBQK1R1 b Qkq - 0 1");
		assertCastling(BLACK_KING_SIDE, true, fen);
	}

	@Test
	void testBlackCantCastleKingSideNotAvailable() {
		Fen fen = new Fen("r1bqk2r/p1pp1ppp/1pnbpn2/8/2B5/4PN2/PPPP1PPP/RNBQK1R1 b Qq - 0 1");
		assertCastling(BLACK_KING_SIDE, false, fen);
	}

	@Test
	void testBlackCanCastleQueenSide() {
		Fen fen = new Fen("r3k2r/p1pp1ppp/1pnbpn2/8/2B5/4PN2/PPPP1PPP/RNBQK1R1 b Qkq - 0 1");
		assertCastling(BLACK_QUEEN_SIDE, true, fen);
	}

	@Test
	void testBlackCantCastleQueenSideNotAvailable() {
		Fen fen = new Fen("r3k2r/p1pp1ppp/1pnbpn2/8/2B5/4PN2/PPPP1PPP/RNBQK1R1 b Qk - 0 1");
		assertCastling(BLACK_QUEEN_SIDE, false, fen);
	}

	@Test
	void testWhiteCantCastleKingSideCastlingAvailableButBlockedByOwnPiece() {
		Fen fen = new Fen("r1bqkb1r/p1pp1ppp/1pn1pn2/8/2B5/4P3/PPPP1PPP/1R2K1NR w Kkq - 0 1");
		Fen fen2 = new Fen("r1bqkb1r/p1pp1ppp/1pn1pn2/8/2B5/4P3/PPPP1PPP/1R2KN1R w Kkq - 0 1");

		assertCastling(WHITE_KING_SIDE, false, fen);
		assertCastling(WHITE_KING_SIDE, false, fen2);
	}

	@Test
	void testWhiteCantCastleQueenSideCastlingAvailableButBlockedByOwnPiece() {
		Stream.of(
				new Fen("r1bqkb1r/p1pp1ppp/1pn1pn2/8/2B5/4P3/PPPP1PPP/RQ2K1NR w KQkq - 0 1"),
				new Fen("r1bqkb1r/p1pp1ppp/1pn1pn2/8/2B5/4P3/PPPP1PPP/R1Q1K1NR w KQkq - 0 1"),
				new Fen("r1bqkb1r/p1pp1ppp/1pn1pn2/8/2B5/4P3/PPPP1PPP/R2QK1NR w KQkq - 0 1")
		).forEach(fen -> assertCastling(WHITE_QUEEN_SIDE, false, fen));
	}

	@Test
	void testWhiteCantCastleKingSideCastlingAvailableButSquaresInCheck() {
		Fen fen = new Fen("r1b1kb1r/p1pp1ppp/1pn1pn2/8/2q5/4P3/PPPP3P/1R2K2R w Kkq - 0 1");
		Fen fen2 = new Fen("r1b1kb1r/p1pp1ppp/1pn1pn2/8/6q1/4P3/PPPP3P/1R2K2R w Kkq - 0 1");

		assertCastling(WHITE_KING_SIDE, false, fen);
		assertCastling(WHITE_KING_SIDE, false, fen2);
	}

	@Test
	void testWhiteCantCastleQueenSideCastlingAvailableButSquaresInCheck() {
		Fen canCastle = new Fen("r1b1kb1r/pqpp1ppp/p1n1pn2/8/2B5/4P3/P4PPP/R3K1NR w KQkq - 0 1");
		Fen cantCastle = new Fen("r1b1kb1r/p1pp2pp/p1n1pn2/2q5/8/4P3/P4PPP/R3K1NR w KQkq - 0 1");
		Fen cantCastle2 = new Fen("r1b1kb1r/p1pp2pp/p1n1pn2/3q4/8/4P3/P4PPP/R3K1NR w KQkq - 0 1");

		assertCastling(WHITE_QUEEN_SIDE, true, canCastle);
		assertCastling(WHITE_QUEEN_SIDE, false, cantCastle);
		assertCastling(WHITE_QUEEN_SIDE, false, cantCastle2);
	}

	@Test
	void testWhiteCantCastleCastlingAvailableButKingInCheck() {
		Fen fen = new Fen("r1b1kb1r/p1pp1ppp/1pn2n2/8/7q/4Pp2/PPPP3P/R3K2R w KQkq - 0 1");
		assertCastling(WHITE_KING_SIDE, false, fen);
		assertCastling(WHITE_QUEEN_SIDE, false, fen);

	}

	@Test
	void testBlackCantCastleKingSideCastlingAvailableButBlockedByOwnPiece() {
		Fen fen = new Fen("r1bqkb1r/p1pp1ppp/1pn1pn2/8/2B5/4P3/PPPP1PPP/1R2K1NR b Kkq - 0 1");
		Fen fen2 = new Fen("r1bqk1nr/p1pp1ppp/1pn1p3/8/2B5/4P3/PPPP1PPP/1R2K1NR b Kkq - 0 1");

		assertCastling(BLACK_KING_SIDE, false, fen);
		assertCastling(BLACK_KING_SIDE, false, fen2);
	}

	@Test
	void testBlackCantCastleQueenSideCastlingAvailableButBlockedByOwnPiece() {
		Stream.of(
				new Fen("r2qk1nr/p1pp1ppp/1pn1p3/2b5/2B5/4P3/PPPP1PPP/1R2K1NR b Kkq - 0 1"),
				new Fen("r1q1k1nr/p1pp1ppp/1pn1p3/2b5/2B5/4P3/PPPP1PPP/1R2K1NR b Kkq - 0 1"),
				new Fen("rq2k1nr/p1pp1ppp/1pn1p3/2b5/2B5/4P3/PPPP1PPP/1R2K1NR b Kkq - 0 1")
		).forEach(fen -> assertCastling(BLACK_QUEEN_SIDE, false, fen));
	}

	@Test
	void testBlackCantCastleKingSideCastlingAvailableButSquaresInCheck() {
		Fen fen = new Fen("r1b1k2r/p1pp3p/1pn1p3/3n4/2q5/4P3/PPPP3P/1R2KR2 b kq - 0 1");
		Fen fen2 = new Fen("r1b1k2r/p1pp3p/1pn1p3/3n4/2q5/4P3/PPPP3P/1R2K1R1 b kq - 0 1");

		assertCastling(BLACK_KING_SIDE, false, fen);
		assertCastling(BLACK_KING_SIDE, false, fen2);
	}

	@Test
	void testBlackCantCastleQueenSideCastlingAvailableButSquaresInCheck() {
		Fen canCastle = new Fen("r3k2r/p2p3p/4p3/1Q1n4/8/4P3/P2P3P/4K1R1 b kq - 0 1");
		Fen cantCastle = new Fen("r3k2r/p2p3p/4p3/Q2n4/8/4P3/P2P3P/4K1R1 b kq - 0 1");
		Fen cantCastle2 = new Fen("r3k2r/p2p3p/4p3/2Qn4/8/4P3/P2P3P/4K1R1 b kq - 0 1");

		assertCastling(BLACK_QUEEN_SIDE, true, canCastle);
		assertCastling(BLACK_QUEEN_SIDE, false, cantCastle);
		assertCastling(BLACK_QUEEN_SIDE, false, cantCastle2);
	}

	@Test
	void testBlackCantCastleCastlingAvailableButKingInCheck() {
		Fen fen = new Fen("r3k2r/p6p/4p3/1Q1n4/8/4P3/P2P3P/4K1R1 b kq - 0 1");

		assertCastling(BLACK_KING_SIDE, false, fen);
		assertCastling(BLACK_QUEEN_SIDE, false, fen);
	}

	@Test
	void testEnPassantNoTargetSquare() {
		Fen fen = new Fen("r1bqkbnr/ppppp1pp/2n5/4Pp2/8/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");

		Map<Position, Set<Position>> legalMoves = getLegalMoves(fen);

		assertThat(legalMoves.get(new Position("e5"))).isNotEmpty().doesNotContain(new Position("f6"));
	}

	@Test
	void testEnPassantLegalMoveWhite() {
		Fen fenRight = new Fen("r1bqkbnr/ppppp1pp/2n5/4Pp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f5 0 1");
		Fen fenLeft = new Fen("r1bqkbnr/ppp1p1pp/2n5/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq d5 0 1");

		Map<Position, Set<Position>> legalMovesRight = getLegalMoves(fenRight);
		Map<Position, Set<Position>> legalMovesLeft = getLegalMoves(fenLeft);

		assertThat(legalMovesRight.get(new Position("e5"))).contains(new Position("f6"));
		assertThat(legalMovesLeft.get(new Position("e5"))).contains(new Position("d6"));
	}

	@Test
	void testEnPassantLegalMoveWhiteEdge() {
		Fen fenRight = new Fen("r1bqkbnr/ppppppp1/2n5/6Pp/8/8/PPPPPP1P/RNBQKBNR w KQkq h5 0 1");
		Fen fenLeft = new Fen("r1bqkbnr/1ppppppp/2n5/pP6/8/8/P1PPPPPP/RNBQKBNR w KQkq a5 0 1");

		Map<Position, Set<Position>> legalMovesRight = getLegalMoves(fenRight);
		Map<Position, Set<Position>> legalMovesLeft = getLegalMoves(fenLeft);

		assertThat(legalMovesRight.get(new Position("g5"))).contains(new Position("h6"));
		assertThat(legalMovesLeft.get(new Position("b5"))).contains(new Position("a6"));
	}

	@Test
	void testEnPassantLegalMoveBlack() {
		Fen fenLeft = new Fen("r1bqkbnr/ppp1p1pp/2n5/8/3pPp2/8/PPPP1PPP/RNBQKBNR b KQkq e4 0 1");
		Fen fenRight = new Fen("r1bqkbnr/ppp1p1pp/2n5/8/3pPp2/8/PPPP1PPP/RNBQKBNR b KQkq e4 0 1");

		Map<Position, Set<Position>> legalMovesLeft = getLegalMoves(fenLeft);
		Map<Position, Set<Position>> legalMovesRight = getLegalMoves(fenRight);

		assertThat(legalMovesLeft.get(new Position("d4"))).contains(new Position("e3"));
		assertThat(legalMovesRight.get(new Position("f4"))).contains(new Position("e3"));
	}

	@Test
	void testEnPassantLegalMoveBlackEdge() {
		Fen fenLeft = new Fen("r1bqkbnr/2pppppp/2n5/p7/Pp6/8/1PPPPPPP/RNBQKBNR b KQkq a4 0 1");
		Fen fenRight = new Fen("r1bqkbnr/2pppp2/2n5/p6p/1p4pP/8/PPPPPPP1/RNBQKBNR b KQkq h4 0 1");

		Map<Position, Set<Position>> legalMovesLeft = getLegalMoves(fenLeft);
		Map<Position, Set<Position>> legalMovesRight = getLegalMoves(fenRight);

		assertThat(legalMovesLeft.get(new Position("b4"))).contains(new Position("a3"));
		assertThat(legalMovesRight.get(new Position("g4"))).contains(new Position("h3"));
	}

	private Map<Position, Set<Position>> getLegalMoves(Fen fen) {
		Game game = new Game(new User("white"), new User("black"), fen);
		return MoveGenerator.generateLegalMoves(game);
	}

	private boolean isMoveLegal(Move move, Map<Position, Set<Position>> legalMoves) {
		return legalMoves.get(move.getFrom()).contains(move.getTo());
	}

	private void assertCastling(CastlingMove castlingMove, boolean expected, Fen fen) {
		Map<Position, Set<Position>> legalMoves = getLegalMoves(fen);

		Position to = castlingMove.getKingTargetPosition();
		Position kingPosition = castlingMove.getKingPosition();

		assertThat(legalMoves.get(kingPosition).contains(to)).isEqualTo(expected);
	}
}