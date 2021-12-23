package com.github.piotrostrow.chess.domain.chess;

import com.github.piotrostrow.chess.domain.User;
import com.github.piotrostrow.chess.ws.dto.Move;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class PgnSerializerTest {

	@Test
	void testSingleMove() {
		Game game = new Game(new User("white"), new User("black"));
		game.moveIfLegal(new Move("e2", "e4"));

		String actual = PgnSerializer.serialize(game);

		assertThat(actual.trim()).isEqualTo("1. e4 *");
	}

	@Test
	void testTwoMoves() {
		Game game = new Game(new User("white"), new User("black"));
		game.moveIfLegal(new Move("e2", "e4"));
		game.moveIfLegal(new Move("e7", "e5"));

		String actual = PgnSerializer.serialize(game);

		assertThat(actual.trim()).isEqualTo("1. e4 e5 *");
	}

	@Test
	void testFourMoves() {
		Game game = new Game(new User("white"), new User("black"));
		game.moveIfLegal(new Move("e2", "e4"));
		game.moveIfLegal(new Move("e7", "e5"));
		game.moveIfLegal(new Move("g1", "f3"));
		game.moveIfLegal(new Move("b8", "c6"));

		String actual = PgnSerializer.serialize(game);

		assertThat(actual.trim()).isEqualTo("1. e4 e5 2. Nf3 Nc6 *");
	}

	@Test
	void testCheckMateWhiteWins() {
		Game game = new Game(new User("white"), new User("black"));
		game.moveIfLegal(new Move("e2", "e4"));
		game.moveIfLegal(new Move("f7", "f6"));
		game.moveIfLegal(new Move("b1", "c3"));
		game.moveIfLegal(new Move("g7", "g5"));
		game.moveIfLegal(new Move("d1", "h5"));

		String actual = PgnSerializer.serialize(game);
		assertThat(actual.trim()).isEqualTo("1. e4 f6 2. Nc3 g5 3. Qh5# 1-0");
	}

	@Test
	void testCheckMateBlackWins() {
		Game game = new Game(new User("white"), new User("black"));
		game.moveIfLegal(new Move("f2", "f3"));
		game.moveIfLegal(new Move("e7", "e5"));
		game.moveIfLegal(new Move("g2", "g4"));
		game.moveIfLegal(new Move("d8", "h4"));

		String actual = PgnSerializer.serialize(game);
		assertThat(actual.trim()).isEqualTo("1. f3 e5 2. g4 Qh4# 0-1");
	}

	@Test
	void testDrawStaleMate() {
		// TODO: starting fen
		Game game = new Game(new User("white"), new User("black"), new Fen("6k1/1Q6/4K3/8/8/8/PPP5/3R4 w - - 0 1"));
		game.moveIfLegal(new Move("b7", "f7"));
		game.moveIfLegal(new Move("g8", "h8"));
		game.moveIfLegal(new Move("d1", "d7"));

		String actual = PgnSerializer.serialize(game);
		assertThat(actual.trim()).isEqualTo("1. Qf7+ Kh8 2. Rd7 1/2-1/2");
	}

	@Test
	void testMaxLineLength() {
		Game game = new Game(new User("white"), new User("black"));

		for (int i = 0; i < 10; i++) {
			game.moveIfLegal(new Move("b1", "c3"));
			game.moveIfLegal(new Move("b8", "c6"));
			game.moveIfLegal(new Move("c3", "b1"));
			game.moveIfLegal(new Move("c6", "b8"));
		}

		String actual = PgnSerializer.serialize(game, true);

		Arrays.stream(actual.split(String.valueOf(PgnSerializer.LINE_BREAK)))
				.forEach(e -> assertThat(e.length()).isLessThanOrEqualTo(PgnSerializer.MAX_LINE_LENGTH));
	}
}