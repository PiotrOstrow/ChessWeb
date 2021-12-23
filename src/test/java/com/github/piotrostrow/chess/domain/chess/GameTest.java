package com.github.piotrostrow.chess.domain.chess;

import com.github.piotrostrow.chess.domain.User;
import com.github.piotrostrow.chess.domain.chess.pieces.*;
import com.github.piotrostrow.chess.ws.dto.Move;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameTest {

	private final User white = new User("white");
	private final User black = new User("white");

	@Test
	void testGameActiveColor() {
		Fen fen = new Fen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		Game game = new Game(white, black, fen);

		assertThat(game.moveIfLegal(new Move("e2", "e4"))).isTrue();
		assertThat(game.moveIfLegal(new Move("d2", "d4"))).isFalse();
		assertThat(game.moveIfLegal(new Move("g8", "f6"))).isTrue();
		assertThat(game.moveIfLegal(new Move("e7", "e5"))).isFalse();
	}

	@Test
	void testGameResultDefaultStartingPosition() {
		assertGameResult("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", GameResult.ONGOING);
	}

	@Test
	void testGameResultCheckMate() {
		assertGameResult("1k6/8/5q2/8/8/8/1q6/1K6 w - - 0 1", GameResult.CHECKMATE);
	}

	@Test
	void testGameResultStalemate() {
		assertGameResult("8/4k3/8/8/8/8/2q5/K7 w - - 0 1", GameResult.STALEMATE);
	}

	@Test
	void testGameResultCheckmateDoubleCheckmate() {
		assertGameResult("5k2/8/r4q2/8/8/8/8/KB3R2 w - - 0 1", GameResult.CHECKMATE);
	}

	@Test
	void testGameResultCheckmateWithPinnedPiecePinnedByPinnedPiece() {
		assertGameResult("5k2/8/4bqq1/8/8/8/8/K4Rr1 w - - 0 1", GameResult.CHECKMATE);
	}

	@Test
	void testStaleMateUnderAPawn() {
		assertGameResult("4k3/8/5r2/8/8/3q4/4p3/4K3 w - - 0 1", GameResult.STALEMATE);
	}

	@Test
	void testMoveWhiteKingSideCastle() {
		Game game = gameFrom("r3k2r/pppqppbp/2npbnp1/6B1/8/2NP1NP1/PPPQPPBP/R3K2R w KQkq - 0 1");

		assertThat(game.moveIfLegal(new Move("e1", "g1"))).isTrue();

		Map<Position, Piece> pieces = game.getPieces();

		assertThat(pieces.get(new Position("g1"))).isInstanceOf(King.class);
		assertThat(pieces.get(new Position("f1"))).isInstanceOf(Rook.class);
		assertThat(pieces.get(new Position("e1"))).isNull();
		assertThat(pieces.get(new Position("h1"))).isNull();
	}

	@Test
	void testMoveWhiteQueenSideCastle() {
		Game game = gameFrom("r3k2r/pppqppbp/2npbnp1/6B1/8/2NP1NP1/PPPQPPBP/R3K2R w KQkq - 0 1");

		assertThat(game.moveIfLegal(new Move("e1", "c1"))).isTrue();

		Map<Position, Piece> pieces = game.getPieces();

		assertThat(pieces.get(new Position("c1"))).isInstanceOf(King.class);
		assertThat(pieces.get(new Position("d1"))).isInstanceOf(Rook.class);
		assertThat(pieces.get(new Position("e1"))).isNull();
		assertThat(pieces.get(new Position("a1"))).isNull();
	}

	@Test
	void testMoveBlackKingSideCastle() {
		Game game = gameFrom("r3k2r/pppqppbp/2npbnp1/6B1/8/2NP1NP1/PPPQPPBP/R3K2R b KQkq - 0 1");

		assertThat(game.moveIfLegal(new Move("e8", "g8"))).isTrue();

		Map<Position, Piece> pieces = game.getPieces();

		assertThat(pieces.get(new Position("g8"))).isInstanceOf(King.class);
		assertThat(pieces.get(new Position("f8"))).isInstanceOf(Rook.class);
		assertThat(pieces.get(new Position("e8"))).isNull();
		assertThat(pieces.get(new Position("h8"))).isNull();
	}

	@Test
	void testMoveBlackQueenSideCastle() {
		Game game = gameFrom("r3k2r/pppqppbp/2npbnp1/6B1/8/2NP1NP1/PPPQPPBP/R3K2R b KQkq - 0 1");

		assertThat(game.moveIfLegal(new Move("e8", "c8"))).isTrue();

		Map<Position, Piece> pieces = game.getPieces();

		assertThat(pieces.get(new Position("c8"))).isInstanceOf(King.class);
		assertThat(pieces.get(new Position("d8"))).isInstanceOf(Rook.class);
		assertThat(pieces.get(new Position("e8"))).isNull();
		assertThat(pieces.get(new Position("a8"))).isNull();
	}

	@Test
	void testCantCastleKingHasMoved() {
		Game game = gameFrom("r3k2r/pppqppbp/2npbnp1/6B1/8/2NP1NP1/PPPQPPBP/R3K2R w KQkq - 0 1");

		assertThat(game.moveIfLegal(new Move("e1", "d1"))).isTrue();
		assertThat(game.moveIfLegal(new Move("e8", "d8"))).isTrue();
		assertThat(game.moveIfLegal(new Move("d1", "e1"))).isTrue();
		assertThat(game.moveIfLegal(new Move("d8", "e8"))).isTrue();

		assertThat(game.moveIfLegal(new Move("e1", "g1"))).isFalse();
		assertThat(game.moveIfLegal(new Move("e1", "c1"))).isFalse();

		assertThat(game.moveIfLegal(new Move("e1", "f1"))).isTrue();

		assertThat(game.moveIfLegal(new Move("e8", "g8"))).isFalse();
		assertThat(game.moveIfLegal(new Move("e8", "c8"))).isFalse();
	}

	@Test
	void testCantCastleKingSideRookHasMoved() {
		Game game = gameFrom("r3k2r/pppqppbp/2npbnp1/6B1/8/2NP1NP1/PPPQPPBP/R3K2R w KQkq - 0 1");

		assertThat(game.moveIfLegal(new Move("h1", "g1"))).isTrue();
		assertThat(game.moveIfLegal(new Move("h8", "g8"))).isTrue();
		assertThat(game.moveIfLegal(new Move("g1", "h1"))).isTrue();
		assertThat(game.moveIfLegal(new Move("g8", "h8"))).isTrue();

		assertThat(game.moveIfLegal(new Move("e1", "g1"))).isFalse();
		assertThat(game.moveIfLegal(new Move("e1", "c1"))).isTrue();

		assertThat(game.moveIfLegal(new Move("e8", "g8"))).isFalse();
		assertThat(game.moveIfLegal(new Move("e8", "c8"))).isTrue();
	}

	@Test
	void testCantCastleQueenSideRookHasMoved() {
		Game game = gameFrom("r3k2r/pppqppbp/2npbnp1/6B1/8/2NP1NP1/PPPQPPBP/R3K2R w KQkq - 0 1");

		assertThat(game.moveIfLegal(new Move("a1", "b1"))).isTrue();
		assertThat(game.moveIfLegal(new Move("a8", "b8"))).isTrue();
		assertThat(game.moveIfLegal(new Move("b1", "a1"))).isTrue();
		assertThat(game.moveIfLegal(new Move("b8", "a8"))).isTrue();

		assertThat(game.moveIfLegal(new Move("e1", "c1"))).isFalse();
		assertThat(game.moveIfLegal(new Move("e1", "g1"))).isTrue();

		assertThat(game.moveIfLegal(new Move("e8", "c8"))).isFalse();
		assertThat(game.moveIfLegal(new Move("e8", "g8"))).isTrue();
	}

	@Test
	void testCastlingAvailabilityRookWasCaptured() {
		Game game = gameFrom("r3k2r/pppqpp1p/2np2p1/3b4/3B4/2NP2P1/PPPQPP1P/R3K2R b KQkq - 0 1");

		assertThat(game.moveIfLegal(new Move("d5", "h1"))).isTrue();
		assertThat(game.moveIfLegal(new Move("d4", "h8"))).isTrue();

		assertThat(game.moveIfLegal(new Move("e8", "g8"))).isFalse();
		assertThat(game.moveIfLegal(new Move("e8", "f8"))).isTrue();

		assertThat(game.moveIfLegal(new Move("e1", "g1"))).isFalse();
	}

	@Test
	void testEnPassantMove() {
		Game game = gameFrom("r1bqkbnr/ppppp1pp/2n5/4Pp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f5 0 1");

		assertThat(game.moveIfLegal(new Move("e5", "f6"))).isTrue();

		Map<Position, Piece> pieces = game.getPieces();

		assertThat(pieces)
				.doesNotContainKey(new Position("e5"))
				.doesNotContainKey(new Position("f5"));

		assertThat(pieces.get(new Position("f6")))
				.isNotNull()
				.isInstanceOf(Pawn.class)
				.extracting(Piece::getColor)
				.isEqualTo(Color.WHITE);

		assertThat(game.getEnPassantTarget()).isEmpty();
	}

	@Test
	void testEnPassantTargetCaptured() {
		Game game = gameFrom("r1bqkbnr/pppppppp/2n5/4P3/8/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");

		assertThat(game.getEnPassantTarget()).isEmpty();
		assertThat(game.moveIfLegal(new Move("e5", "f6"))).isFalse();
		assertThat(game.moveIfLegal(new Move("g1", "f3"))).isTrue();
		assertThat(game.moveIfLegal(new Move("f7", "f5"))).isTrue();
		assertThat(game.getEnPassantTarget()).isPresent().get().isEqualTo(new Position("f5"));
		assertThat(game.moveIfLegal(new Move("e5", "f6"))).isTrue();
	}

	@Test
	void testEnPassantTargetNotCaptured() {
		Game game = gameFrom("r1bqkbnr/pppppppp/2n5/4P3/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1");

		assertThat(game.moveIfLegal(new Move("f7", "f5"))).isTrue();
		assertThat(game.getEnPassantTarget()).isPresent().get().isEqualTo(new Position("f5"));
		assertThat(game.moveIfLegal(new Move("e5", "e6"))).isTrue();
		assertThat(game.getEnPassantTarget()).isEmpty();
	}

	@Test
	void testPromoteWhitePawn() {
		Game game = gameFrom("6k1/3PK1p1/5pPp/5P2/p7/P7/1p5P/8 w - - 0 1");
		assertThat(game.moveIfLegal(new Move("d7", "d8"))).isTrue();
		assertThat(game.getPieces().get(new Position("d8")))
				.isInstanceOf(Queen.class)
				.extracting(Piece::getColor)
				.isEqualTo(Color.WHITE);
	}

	@Test
	void testPromoteBlackPawn() {
		Game game = gameFrom("6k1/3PK1p1/5pPp/5P2/p7/P7/1p5P/8 b - - 0 1");
		assertThat(game.moveIfLegal(new Move("b2", "b1"))).isTrue();
		assertThat(game.getPieces().get(new Position("b1")))
				.isInstanceOf(Queen.class)
				.extracting(Piece::getColor)
				.isEqualTo(Color.BLACK);
	}

	@Test
	void testPromoteWhitePawnWithCapture() {
		Game game = gameFrom("rnbqkbnr/p1pppppP/8/8/8/8/pPPPPP1P/RNBQKBNR w KQkq - 0 1");

		assertThat(game.moveIfLegal(new Move("h7", "g8"))).isTrue();
		assertThat(game.getPieces().get(new Position("g8")))
				.isInstanceOf(Queen.class)
				.extracting(Piece::getColor)
				.isEqualTo(Color.WHITE);
	}

	@Test
	void testPromoteBlackPawnWithCapture() {
		Game game = gameFrom("rnbqkbnr/p1pppppP/8/8/8/8/pPPPPP1P/RNBQKBNR b KQkq - 0 1");

		assertThat(game.moveIfLegal(new Move("a2", "b1"))).isTrue();
		assertThat(game.getPieces().get(new Position("b1")))
				.isInstanceOf(Queen.class)
				.extracting(Piece::getColor)
				.isEqualTo(Color.BLACK);
	}

	@Test
	void testGetWinnerGameOngoingShouldThrowIllegalStateException() {
		Game game = new Game(white, black, new Fen("6k1/2Q5/4K3/8/8/8/PPP1R3/8 b - - 0 1"));
		assertThat(game.getGameResult()).isEqualTo(GameResult.ONGOING);
		assertThatThrownBy(game::getWinner).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void testGetWinnerGameEndedInADrawShouldThrowIllegalStateException() {
		// TODO implement draw - other than by stalemate
	}

	@Test
	void testGetWinnerGameEndedInStaleMateShouldThrowIllegalStateException() {
		Game game = new Game(white, black, new Fen("7k/5Q2/4K3/3R4/8/8/PPP5/8 b - - 0 1"));
		assertThat(game.getGameResult()).isEqualTo(GameResult.STALEMATE);
		assertThatThrownBy(game::getWinner).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void testGetWinnerWhite() {
		Game game = new Game(white, black, new Fen("rnbqkbnr/ppppp2p/5p2/6pQ/4P3/2N5/PPPP1PPP/R1B1KBNR b KQkq - 1 3"));
		assertThat(game.getGameResult()).isEqualTo(GameResult.CHECKMATE);
		assertThat(game.getWinner()).isEqualTo(Color.WHITE);
	}

	@Test
	void testGetWinnerBlack() {
		Game game = new Game(white, black, new Fen("rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq - 1 3"));
		assertThat(game.getGameResult()).isEqualTo(GameResult.CHECKMATE);
		assertThat(game.getWinner()).isEqualTo(Color.BLACK);
	}

	private Game gameFrom(String fen) {
		return new Game(white, black, new Fen(fen));
	}

	private void assertGameResult(String s, GameResult checkmate) {
		Fen fen = new Fen(s);
		Game game = new Game(white, black, fen);

		assertThat(game.getGameResult()).isEqualTo(checkmate);
	}
}