package com.github.piotrostrow.chess.ws;

import com.github.piotrostrow.chess.ws.dto.Move;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameSessionTest {

	private final Principal white = new UsernamePasswordAuthenticationToken("white123", "");
	private final Principal black = new UsernamePasswordAuthenticationToken("black132", "");

	@Test
	void testLegalMoveCorrectUserShouldReturnTrue() {
		GameSession gameSession = new GameSession(white, black);

		assertThat(gameSession.move(new Move("e2", "e4"), white)).isTrue();
		assertThat(gameSession.move(new Move("e7", "e5"), black)).isTrue();
	}

	@Test
	void testLegalMoveIncorrectUserShouldReturnFalse() {
		GameSession gameSession = new GameSession(white, black);

		assertThat(gameSession.move(new Move("e7", "e5"), black)).isFalse();
	}

	@Test
	void testIllegalMoveCorrectUserShouldReturnFalse() {
		GameSession gameSession = new GameSession(white, black);

		assertThat(gameSession.move(new Move("d1", "h5"), white)).isFalse();
	}

	@Test
	void testUserNotInSessionShouldThrowIllegalArgumentException() {
		GameSession gameSession = new GameSession(white, black);
		Principal thirdUser = new UsernamePasswordAuthenticationToken("smith", "");

		Move move = new Move("e2", "e4");
		assertThatThrownBy(() -> gameSession.move(move, thirdUser))
				.isInstanceOf(IllegalArgumentException.class);
	}
}