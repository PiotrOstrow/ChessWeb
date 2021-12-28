package com.github.piotrostrow.chess.ws.service;

import com.github.piotrostrow.chess.domain.chess.GameResult;
import com.github.piotrostrow.chess.rest.serivce.GameService;
import com.github.piotrostrow.chess.ws.dto.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameManagerTest {

	private final Principal white = new UsernamePasswordAuthenticationToken("white123", "");
	private final Principal black = new UsernamePasswordAuthenticationToken("black321", "");

	private ThreadPoolTaskScheduler threadPoolTaskScheduler;
	private WebSocketService webSocketService;
	private GameService gameService;

	@BeforeEach
	void setUp() {
		threadPoolTaskScheduler = mock(ThreadPoolTaskScheduler.class);
		doReturn(mock(ScheduledFuture.class)).when(threadPoolTaskScheduler).schedule(any(), any(Instant.class));

		webSocketService = mock(WebSocketService.class);
		gameService = mock(GameService.class);
	}

	@Test
	void testStartGame() {
		GameManager gameManager = new GameManager(webSocketService, gameService, threadPoolTaskScheduler);

		gameManager.startGame(white, black);

		verify(webSocketService, times(1)).sendStartGame(eq(white.getName()), eq(black.getName()), anyInt());
		assertThat(gameManager.isPlaying(white)).isTrue();
		assertThat(gameManager.isPlaying(black)).isTrue();
	}

	@Test
	void testStartGameAlreadyPlaying() {
		GameManager gameManager = new GameManager(webSocketService, gameService, threadPoolTaskScheduler);
		gameManager.startGame(white, black);

		Principal thirdUser = new UsernamePasswordAuthenticationToken("third_user", "");
		gameManager.startGame(white, thirdUser);

		assertThat(gameManager.isPlaying(thirdUser)).isFalse();
	}

	@Test
	void testMoves() {
		GameManager gameManager = new GameManager(webSocketService, gameService, threadPoolTaskScheduler);
		gameManager.startGame(white, black);

		Move whiteMove = new Move("e2", "e4");
		Move blackMove = new Move("e7", "e5");

		gameManager.move(white, whiteMove);
		verify(webSocketService, times(2)).sendMove(any(), any()); // TODO: capture arguments

		gameManager.move(black, blackMove);
		verify(webSocketService, times(4)).sendMove(any(), any());

		verify(gameService, never()).saveGame(any());
	}

	@Test
	void testMoveNoGame() {
		GameManager gameManager = new GameManager(webSocketService, gameService, threadPoolTaskScheduler);

		gameManager.move(white, new Move("e2", "e4"));

		verify(webSocketService, never()).sendMove(any(), any());
		verify(gameService, never()).saveGame(any());
	}

	@Test
	void testMoveNotLegal() {
		GameManager gameManager = new GameManager(webSocketService, gameService, threadPoolTaskScheduler);
		gameManager.startGame(white, black);

		assertThat(gameManager.isPlaying(white)).isTrue();
		gameManager.move(white, new Move("e2", "f4"));

		verify(webSocketService, never()).sendMove(any(), any());
		verify(gameService, never()).saveGame(any());
	}

	@Test
	void testMoveNotPlayersTurn() {
		GameManager gameManager = new GameManager(webSocketService, gameService, threadPoolTaskScheduler);
		gameManager.startGame(white, black);

		assertThat(gameManager.isPlaying(black)).isTrue();
		gameManager.move(black, new Move("e7", "e6"));

		verify(webSocketService, never()).sendMove(any(), any());
		verify(gameService, never()).saveGame(any());
	}

	@Test
	void testMoveGameOverByCheckmate() {
		GameManager gameManager = new GameManager(webSocketService, gameService, threadPoolTaskScheduler);
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
		GameManager gameManager = new GameManager(webSocketService, gameService, threadPoolTaskScheduler);
		gameManager.startGame(white, black);

		gameManager.onDisconnect(white);

		verify(webSocketService, times(1)).sendGameOver(black.getName(), GameResult.DISCONNECTED);
		verify(webSocketService, never()).sendGameOver(white.getName(), GameResult.DISCONNECTED);
		assertThat(gameManager.isPlaying(white)).isFalse();
		assertThat(gameManager.isPlaying(black)).isFalse();
	}
}