package com.github.piotrostrow.chess.domain.chess;

import com.github.piotrostrow.chess.domain.User;
import com.github.piotrostrow.chess.ws.dto.Move;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
		assertGameResult("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", GameResult.NONE);
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

	private void assertGameResult(String s, GameResult checkmate) {
		Fen fen = new Fen(s);
		Game game = new Game(white, black, fen);

		assertThat(game.getGameResult()).isEqualTo(checkmate);
	}
}