package com.github.piotrostrow.chess.ws.service;

import com.github.piotrostrow.chess.domain.chess.GameResult;
import com.github.piotrostrow.chess.rest.serivce.GameService;
import com.github.piotrostrow.chess.ws.dto.Move;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameManagerTest {

	private final Principal white = new UsernamePasswordAuthenticationToken("white123", "");
	private final Principal black = new UsernamePasswordAuthenticationToken("black321", "");

	@Test
	void testStartGame() {
		WebSocketService webSocketService = mock(WebSocketService.class);
		GameService gameService = mock(GameService.class);
		GameManager gameManager = new GameManager(webSocketService, gameService);

		gameManager.startGame(white, black);

		verify(webSocketService, times(1)).sendStartGame(white.getName(), black.getName());
		assertThat(gameManager.isPlaying(white)).isTrue();
		assertThat(gameManager.isPlaying(black)).isTrue();
	}

	@Test
	void testMoves() {
		WebSocketService webSocketService = mock(WebSocketService.class);
		GameService gameService = mock(GameService.class);
		GameManager gameManager = new GameManager(webSocketService, gameService);
		gameManager.startGame(white, black);

		Move whiteMove = new Move("e2", "e4");
		Move blackMove = new Move("e7", "e5");

		gameManager.move(white, whiteMove);
		verify(webSocketService, times(1)).sendMove(black.getName(), whiteMove);

		gameManager.move(black, blackMove);
		verify(webSocketService, times(1)).sendMove(white.getName(), blackMove);

		verify(gameService, never()).saveGame(any());
	}

	@Test
	void testMoveNoGame() {
		WebSocketService webSocketService = mock(WebSocketService.class);
		GameService gameService = mock(GameService.class);
		GameManager gameManager = new GameManager(webSocketService, gameService);

		gameManager.move(white, new Move("e2", "e4"));

		verify(webSocketService, never()).sendMove(any(), any());
		verify(gameService, never()).saveGame(any());
	}

	@Test
	void testMoveNotLegal() {
		WebSocketService webSocketService = mock(WebSocketService.class);
		GameService gameService = mock(GameService.class);
		GameManager gameManager = new GameManager(webSocketService, gameService);
		gameManager.startGame(white, black);

		assertThat(gameManager.isPlaying(white)).isTrue();
		gameManager.move(white, new Move("e2", "f4"));

		verify(webSocketService, never()).sendMove(any(), any());
		verify(gameService, never()).saveGame(any());
	}

	@Test
	void testMoveNotPlayersTurn() {
		WebSocketService webSocketService = mock(WebSocketService.class);
		GameService gameService = mock(GameService.class);
		GameManager gameManager = new GameManager(webSocketService, gameService);
		gameManager.startGame(white, black);

		assertThat(gameManager.isPlaying(black)).isTrue();
		gameManager.move(black, new Move("e7", "e6"));

		verify(webSocketService, never()).sendMove(any(), any());
		verify(gameService, never()).saveGame(any());
	}

	@Test
	void testMoveGameOverByCheckmate() {
		WebSocketService webSocketService = mock(WebSocketService.class);
		GameService gameService = mock(GameService.class);
		GameManager gameManager = new GameManager(webSocketService, gameService);
		gameManager.startGame(white, black);

		Move whiteMove1 = new Move("f2", "f3");
		Move blackMove1 = new Move("e7", "e5");
		Move whiteMove2 = new Move("g2", "g4");
		Move blackMove2 = new Move("d8", "h4");

		gameManager.move(white, whiteMove1);
		gameManager.move(black, blackMove1);
		gameManager.move(white, whiteMove2);
		gameManager.move(black, blackMove2);

		verify(gameService, times(1)).saveGame(any());
		verify(webSocketService, times(1)).sendGameOver(white.getName(), GameResult.CHECKMATE);
		verify(webSocketService, times(1)).sendGameOver(black.getName(), GameResult.CHECKMATE);
		assertThat(gameManager.isPlaying(white)).isFalse();
		assertThat(gameManager.isPlaying(black)).isFalse();
	}

	@Test
	void testEndGameByDisconnect() {
		WebSocketService webSocketService = mock(WebSocketService.class);
		GameService gameService = mock(GameService.class);
		GameManager gameManager = new GameManager(webSocketService, gameService);
		gameManager.startGame(white, black);

		gameManager.disconnected(white);

		verify(webSocketService, times(1)).sendGameOver(black.getName(), GameResult.DISCONNECTED);
		verify(webSocketService, never()).sendGameOver(white.getName(), GameResult.DISCONNECTED);
		assertThat(gameManager.isPlaying(white)).isFalse();
		assertThat(gameManager.isPlaying(black)).isFalse();
	}
}