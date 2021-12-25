package com.github.piotrostrow.chess.domain.chess;

import com.github.piotrostrow.chess.ws.dto.Move;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MoveNotationTest {

	@Test
	void testPawnMove() {
		MoveNotation actual = notationFrom(Fen.DEFAULT_STARTING_POSITION, new Move("e2", "e4"));

		assertThat(actual.getNotation()).isEqualTo("e4");
	}

	@Test
	void testKnightMove() {
		MoveNotation actual = notationFrom(Fen.DEFAULT_STARTING_POSITION, new Move("g1", "f3"));

		assertThat(actual.getNotation()).isEqualTo("Nf3");
	}

	@Test
	void testPawnMoveCapture() {
		MoveNotation moveNotation = notationFrom("rnbqkb1r/ppp2ppp/3p1n2/4P3/8/5N2/PPPP1PPP/RNBQKB1R w KQkq - 0 6", new Move("e5", "d6"));

		assertThat(moveNotation.getNotation()).isEqualTo("exd6");
	}

	@Test
	void testPawnMoveEnPassantCapture() {
		MoveNotation moveNotation = notationFrom("rnbqkb1r/ppp1pppp/5n2/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq d5 0 3", new Move("e5", "d6"));

		assertThat(moveNotation.getNotation()).isEqualTo("exd6");
	}

	@Test
	void testKnightMoveCapture() {
		MoveNotation actual = notationFrom("rnbqkb1r/pppp1ppp/5n2/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 1 3", new Move("f3", "e5"));

		assertThat(actual.getNotation()).isEqualTo("Nxe5");
	}

	@Test
	void testBishopCaptureIntoCheck() {
		MoveNotation moveNotation = notationFrom("r1bqkb1r/ppp2p1p/2n2np1/1B1pP3/4P3/8/PPPP2PP/RNBQK1NR w KQkq - 0 6", new Move("b5", "c6"));

		assertThat(moveNotation.getNotation()).isEqualTo("Bxc6+");
	}

	@Test
	void testMoveIntoDiscoveredCheck() {
		MoveNotation moveNotation = notationFrom("r1bqkb1r/ppp2p1p/2N2np1/1B1pP3/4P3/8/PPPP2PP/R1BQK1NR w KQkq - 1 12", new Move("c6", "d4"));

		assertThat(moveNotation.getNotation()).isEqualTo("Nd4+");
	}

	@Test
	void testQueenMoveIntoCheckmate() {
		MoveNotation moveNotation = notationFrom("rnbqkbnr/pppp1ppp/8/4p3/6P1/5P2/PPPPP2P/RNBQKBNR b KQkq g3 0 2", new Move("d8", "h4"));

		assertThat(moveNotation.getNotation()).isEqualTo("Qh4#");
	}

	@Test
	void testKingMoveIntoStalemate() {
		MoveNotation moveNotation = notationFrom("8/4k3/8/8/8/8/2q5/K7 b - - 0 1", new Move("e7", "d6"));

		assertThat(moveNotation.getNotation()).isEqualTo("Kd6");
	}

	@Test
	void testKnightAmbiguousMoveDifferentFile() {
		MoveNotation moveNotationH = notationFrom("8/8/3k3n/8/3n4/8/3q4/1K6 b - - 7 5", new Move("h6", "f5"));
		MoveNotation moveNotationD = notationFrom("8/8/3k3n/8/3n4/8/3q4/1K6 b - - 7 5", new Move("d4", "f5"));

		assertThat(moveNotationH.getNotation()).isEqualTo("Nhf5");
		assertThat(moveNotationD.getNotation()).isEqualTo("Ndf5");
	}

	@Test
	void testKnightAmbiguousMoveSameFile() {
		MoveNotation moveNotation4 = notationFrom("8/8/3n4/4k3/3n4/8/3q4/K7 b - - 21 12", new Move("d4", "f5"));
		MoveNotation moveNotation6 = notationFrom("8/8/3n4/4k3/3n4/8/3q4/K7 b - - 21 12", new Move("d6", "f5"));

		assertThat(moveNotation4.getNotation()).isEqualTo("N4f5");
		assertThat(moveNotation6.getNotation()).isEqualTo("N6f5");
	}

	@Test
	void testKnightAmbiguousMoveDifferentFileCapture() {
		MoveNotation moveNotation4 = notationFrom("8/8/3n4/4kP2/3n4/8/3q4/K7 b - - 29 16", new Move("d4", "f5"));
		MoveNotation moveNotation6 = notationFrom("8/8/3n4/4kP2/3n4/8/3q4/K7 b - - 29 16", new Move("d6", "f5"));

		assertThat(moveNotation4.getNotation()).isEqualTo("N4xf5");
		assertThat(moveNotation6.getNotation()).isEqualTo("N6xf5");
	}

	@Test
	void testKnightAmbiguousMoveSameFileCapture() {
		MoveNotation moveNotation4 = notationFrom("8/8/3n4/4kP2/3n4/8/3q4/K7 b - - 29 16", new Move("d4", "f5"));
		MoveNotation moveNotation6 = notationFrom("8/8/3n4/4kP2/3n4/8/3q4/K7 b - - 29 16", new Move("d6", "f5"));

		assertThat(moveNotation4.getNotation()).isEqualTo("N4xf5");
		assertThat(moveNotation6.getNotation()).isEqualTo("N6xf5");
	}

	@Test
	void testWhiteKingCastleKingSide() {
		MoveNotation moveNotation4 = notationFrom("8/8/3n3n/4kP2/8/8/3q4/K7 b - - 41 22", new Move("h6", "f5"));
		MoveNotation moveNotation6 = notationFrom("8/8/3n3n/4kP2/8/8/3q4/K7 b - - 41 22", new Move("d6", "f5"));

		assertThat(moveNotation4.getNotation()).isEqualTo("Nhxf5");
		assertThat(moveNotation6.getNotation()).isEqualTo("Ndxf5");
	}

	@Test
	void testAmbiguousQueenMove() {
		MoveNotation moveNotation = notationFrom("8/8/3n3n/4kP2/8/8/3q2q1/1K6 b - - 43 23", new Move("g2", "e2"));

		assertThat(moveNotation.getNotation()).isEqualTo("Qge2");
	}

	@Test
	void testWhiteKingCastleQueenSide() {
		MoveNotation moveNotation = notationFrom("r3kbnr/pbppqppp/1pn5/4p3/8/1PN1P3/PBPPQPPP/R3KBNR w KQkq - 2 6", new Move("e1", "c1"));

		assertThat(moveNotation.getNotation()).isEqualTo("O-O-O");
	}

	@Test
	void testBlackKingCastleKingSide() {
		MoveNotation moveNotation = notationFrom("rnbqk2r/ppppppbp/5np1/8/6P1/5N2/PPPPPPBP/RNBQ1RK1 b kq - 3 4", new Move("e8", "g8"));

		assertThat(moveNotation.getNotation()).isEqualTo("O-O");
	}

	@Test
	void testBlackKingCastleQueenSide() {
		MoveNotation moveNotation = notationFrom("r3kbnr/pbppqppp/1pn5/4p3/8/1PN1P3/PBPPQPPP/R3KBNR b KQkq - 2 6", new Move("e8", "c8"));

		assertThat(moveNotation.getNotation()).isEqualTo("O-O-O");
	}

	@Test
	void testCastleIntoCheck() {
		MoveNotation moveNotation = notationFrom("r2k1bnr/ppp1pppp/8/8/8/8/PPP1PPPP/R3KBNR w KQkq - 0 1", new Move("e1", "c1"));

		assertThat(moveNotation.getNotation()).isEqualTo("O-O-O+");
	}

	@Test
	void testPawnPromote() {
		MoveNotation moveNotation = notationFrom("1kr5/ppp2P2/8/8/8/8/PPP5/2KR4 w - - 0 1", new Move("f7", "f8"));

		assertThat(moveNotation.getNotation()).isEqualTo("f8=Q");
	}

	@Test
	void testPawnPromoteWithCapture() {
		MoveNotation moveNotation = notationFrom("4r3/pkp2P2/1p6/8/8/8/PPP5/2KR4 w - - 6 7", new Move("f7", "e8"));

		assertThat(moveNotation.getNotation()).isEqualTo("fxe8=Q");
	}

	@Test
	void testPawnPromoteIntoCheck() {
		MoveNotation moveNotation = notationFrom("1k6/p1p2P2/1p6/8/7r/8/PPP5/2K1R3 w - - 0 4", new Move("f7", "f8"));

		assertThat(moveNotation.getNotation()).isEqualTo("f8=Q+");
	}

	@Test
	void testPawnPromoteWithCaptureIntoCheck() {
		MoveNotation moveNotation = notationFrom("1k2r3/p1p2P2/1p6/8/8/8/PPP5/2K1R3 w - - 4 6", new Move("f7", "e8"));

		assertThat(moveNotation.getNotation()).isEqualTo("fxe8=Q+");
	}

	@Test
	void testPawnPromoteWithCaptureIntoCheckMate() {
		MoveNotation moveNotation = notationFrom("1k2r3/ppp2P2/8/8/8/8/PPP5/2KR4 w - - 4 3", new Move("f7", "e8"));

		assertThat(moveNotation.getNotation()).isEqualTo("fxe8=Q#");
	}

	@Test
	void testStackedPawnWithOneOnInitialPosition() {
		MoveNotation moveNotation = notationFrom("4Q3/1pp4p/1p1p2pk/8/1PB5/P7/2PP1PPP/6K1 b - - 0 22", new Move("b6", "b5"));

		assertThat(moveNotation.getNotation()).isEqualTo("b5");
	}

	private MoveNotation notationFrom(String fen, Move move) {
		return notationFrom(new Fen(fen), move);
	}

	private MoveNotation notationFrom(Fen fen, Move move) {
		Game game = new Game(fen);
		game.moveIfLegal(move);
		List<MoveNotation> moves = game.getMoves();
		return moves.get(moves.size() - 1);
	}

}