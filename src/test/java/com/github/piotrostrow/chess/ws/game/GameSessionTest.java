package com.github.piotrostrow.chess.ws.game;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.Fen;
import com.github.piotrostrow.chess.domain.chess.GameResult;
import com.github.piotrostrow.chess.ws.dto.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.matchers.GreaterOrEqual;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.Mockito.*;

class GameSessionTest {

	private final Principal white = new UsernamePasswordAuthenticationToken("white123", "");
	private final Principal black = new UsernamePasswordAuthenticationToken("black132", "");

	// TODO: verify when onTimeOut is and is not called

	private TaskScheduler taskScheduler;
	private ScheduledFuture<?> scheduledFuture;
	private BiConsumer<GameSession, GameResult> onTimeOut;

	@BeforeEach
	void setUp() {
		taskScheduler = mock(TaskScheduler.class);
		scheduledFuture = mock(ScheduledFuture.class);
		onTimeOut = (a1, a2) -> {
		};

		doReturn(scheduledFuture).when(taskScheduler).schedule(any(), any(Instant.class));
		doReturn(true).when(scheduledFuture).cancel(false);
	}

	@Test
	void testLegalMoveCorrectUserShouldReturnTrue() {
		GameSession gameSession = new GameSession(white, black, taskScheduler, onTimeOut);
		verify(taskScheduler, times(1)).schedule(any(), any(Instant.class));

		assertThat(gameSession.move(new Move("e2", "e4"), white)).isPresent();
		verify(taskScheduler, times(2)).schedule(any(), any(Instant.class));
		verify(scheduledFuture, times(1)).cancel(false);

		assertThat(gameSession.move(new Move("e7", "e5"), black)).isPresent();
		verify(taskScheduler, times(3)).schedule(any(), any(Instant.class));
		verify(scheduledFuture, times(2)).cancel(false);

		assertThat(gameSession.getGameResult()).isEqualTo(GameResult.ONGOING);
		assertThat(gameSession.getWinner()).isEmpty();
	}

	@Test
	void testLegalMoveIncorrectUserShouldReturnFalse() {
		GameSession gameSession = new GameSession(white, black, taskScheduler, onTimeOut);

		assertThat(gameSession.move(new Move("e7", "e5"), black)).isEmpty();
		verify(taskScheduler, times(1)).schedule(any(), any(Instant.class));
		verify(scheduledFuture, never()).cancel(anyBoolean());

		assertThat(gameSession.getGameResult()).isEqualTo(GameResult.ONGOING);
		assertThat(gameSession.getWinner()).isEmpty();
	}

	@Test
	void testIllegalMoveCorrectUserShouldReturnFalse() {
		GameSession gameSession = new GameSession(white, black, taskScheduler, onTimeOut);

		assertThat(gameSession.move(new Move("d1", "h5"), white)).isEmpty();
		verify(taskScheduler, times(1)).schedule(any(), any(Instant.class));
		verify(scheduledFuture, never()).cancel(anyBoolean());

		assertThat(gameSession.getGameResult()).isEqualTo(GameResult.ONGOING);
		assertThat(gameSession.getWinner()).isEmpty();
	}

	@Test
	void testUserNotInSessionShouldThrowIllegalArgumentException() {
		GameSession gameSession = new GameSession(white, black, taskScheduler, onTimeOut);
		Principal thirdUser = new UsernamePasswordAuthenticationToken("smith", "");

		Move move = new Move("e2", "e4");
		assertThatThrownBy(() -> gameSession.move(move, thirdUser)).isInstanceOf(IllegalArgumentException.class);

		verify(taskScheduler, times(1)).schedule(any(), any(Instant.class));
		verify(scheduledFuture, never()).cancel(anyBoolean());

		assertThat(gameSession.getGameResult()).isEqualTo(GameResult.ONGOING);
		assertThat(gameSession.getWinner()).isEmpty();
	}

	@Test
	void testGetGameResultCheckmateWhiteWins() {
		GameSession gameSession = new GameSession(white, black, taskScheduler, onTimeOut);

		assertThat(gameSession.move(new Move("e2", "e4"), white)).isPresent();
		assertThat(gameSession.move(new Move("f7", "f6"), black)).isPresent();
		assertThat(gameSession.move(new Move("d2", "d3"), white)).isPresent();
		assertThat(gameSession.move(new Move("g7", "g5"), black)).isPresent();
		assertThat(gameSession.move(new Move("d1", "h5"), white)).isPresent();

		assertThat(gameSession.getGameResult()).isEqualTo(GameResult.CHECKMATE);
		assertThat(gameSession.getWinner()).isPresent().contains(Color.WHITE);
	}

	@Test
	void testGetWinnerCheckmateBlackWins() {
		GameSession gameSession = new GameSession(white, black, taskScheduler, onTimeOut);

		assertThat(gameSession.move(new Move("f2", "f3"), white)).isPresent();
		assertThat(gameSession.move(new Move("e7", "e6"), black)).isPresent();
		assertThat(gameSession.move(new Move("g2", "g4"), white)).isPresent();
		assertThat(gameSession.move(new Move("d8", "h4"), black)).isPresent();

		assertThat(gameSession.getGameResult()).isEqualTo(GameResult.CHECKMATE);
		assertThat(gameSession.getWinner()).isPresent().contains(Color.BLACK);
	}

	@Test
	void testDrawNoWinner() {
		GameSession gameSession = new GameSession(white, black, taskScheduler, onTimeOut, 180, new Fen("2Q2bnr/4p1pq/5pkr/7p/7P/4P3/PPPP1PP1/RNB1KBNR w KQ - 1 10"));

		assertThat(gameSession.move(new Move("c8", "e6"), white)).isPresent();
		assertThat(gameSession.getGameResult()).isEqualTo(GameResult.STALEMATE);
		assertThat(gameSession.getWinner()).isEmpty();
	}

	@Test
	void testMovedTimeoutFutureCancelledTimeSubtracted() {
		Player whitePlayer = mockPlayer(white.getName(), Color.WHITE, 180 * 1000);
		Player blackPlayer = mockPlayer(black.getName(), Color.BLACK, 180 * 1000);
		GameSession gameSession = new GameSession(whitePlayer, blackPlayer, taskScheduler, onTimeOut, 180, Fen.DEFAULT_STARTING_POSITION);

		assertThat(gameSession.move(new Move("e2", "e4"), white)).isPresent();

		verify(whitePlayer, times(1)).subtractTime(longThat(new GreaterOrEqual<>(0L)));
		verify(scheduledFuture, times(1)).cancel(false);

		assertThat(gameSession.move(new Move("e7", "e5"), black)).isPresent();

		verify(blackPlayer, times(1)).subtractTime(longThat(new GreaterOrEqual<>(0L)));
		verify(scheduledFuture, times(2)).cancel(false);
	}

	@Test
	void testGameSessionTimeoutWhiteWins() {
		Player whitePlayer = mockPlayer(white.getName(), Color.WHITE, 180 * 1000);
		Player blackPlayer = mockPlayer(black.getName(), Color.BLACK, 0);
		GameSession gameSession = new GameSession(whitePlayer, blackPlayer, taskScheduler, onTimeOut, 180, Fen.DEFAULT_STARTING_POSITION);

		ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
		verify(taskScheduler, times(1)).schedule(argumentCaptor.capture(), any(Instant.class));

		assertThat(gameSession.move(new Move("e2", "e4"), white)).isPresent();

		verify(whitePlayer, times(1)).subtractTime(anyLong());
		verify(scheduledFuture, times(1)).cancel(false);

		argumentCaptor.getValue().run();

		assertThat(gameSession.getGameResult()).isEqualTo(GameResult.TIMEOUT);
		assertThat(gameSession.getWinner()).isPresent().contains(Color.WHITE);
	}

	@Test
	void testGameSessionTimeoutBlackWins() {
		Player whitePlayer = mockPlayer(white.getName(), Color.WHITE, 0);
		Player blackPlayer = mockPlayer(black.getName(), Color.BLACK, 180 * 1000);
		GameSession gameSession = new GameSession(whitePlayer, blackPlayer, taskScheduler, onTimeOut, 180, Fen.DEFAULT_STARTING_POSITION);

		ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
		verify(taskScheduler, times(1)).schedule(argumentCaptor.capture(), any(Instant.class));

		argumentCaptor.getValue().run();

		assertThat(gameSession.getWinner()).isPresent().contains(Color.BLACK);
	}

	@Test
	void testScheduledFutureRunningShouldNotReschedule() {
		GameSession gameSession = new GameSession(white, black, taskScheduler, onTimeOut, 180, Fen.DEFAULT_STARTING_POSITION);
		verify(taskScheduler, times(1)).schedule(any(), any(Instant.class));

		doReturn(false).when(scheduledFuture).cancel(false);

		assertThat(gameSession.move(new Move("e2", "e4"), white)).isPresent();
		verify(taskScheduler, times(1)).schedule(any(), any(Instant.class));
	}

	@Test
	void testWhiteDisconnectsFutureCancelledMovesNotAllowed() {
		GameSession gameSession = new GameSession(white, black, taskScheduler, onTimeOut);
		verify(taskScheduler, times(1)).schedule(any(), any(Instant.class));
		gameSession.disconnected(white);

		assertThat(gameSession.getGameResult()).isEqualTo(GameResult.DISCONNECTED);
		assertThat(gameSession.getWinner()).isPresent().contains(Color.BLACK);

		verify(scheduledFuture, times(1)).cancel(false);
		verify(taskScheduler, times(1)).schedule(any(), any(Instant.class));
	}

	@Test
	void testBlackDisconnectsFutureCancelledMovesNotAllowed() {
		GameSession gameSession = new GameSession(white, black, taskScheduler, onTimeOut);
		verify(taskScheduler, times(1)).schedule(any(), any(Instant.class));
		gameSession.disconnected(black);

		assertThat(gameSession.getGameResult()).isEqualTo(GameResult.DISCONNECTED);
		assertThat(gameSession.getWinner()).isPresent().contains(Color.WHITE);

		verify(scheduledFuture, times(1)).cancel(false);
		verify(taskScheduler, times(1)).schedule(any(), any(Instant.class));
	}

	@Test
	void testOnePlayerDisconnectedOpponentCannotMakeMoves() {
		GameSession gameSession = new GameSession(white, black, taskScheduler, onTimeOut);
		gameSession.disconnected(black);

		assertThat(gameSession.move(new Move("e2", "e4"), white)).isEmpty();
	}

	private Player mockPlayer(String name, Color color, long time) {
		Player player = mock(Player.class);
		when(player.getName()).thenReturn(name);
		when(player.getColor()).thenReturn(color);
		when(player.getTime()).thenReturn(time);
		return player;
	}
}