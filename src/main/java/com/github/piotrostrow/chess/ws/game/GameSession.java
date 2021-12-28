package com.github.piotrostrow.chess.ws.game;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.Fen;
import com.github.piotrostrow.chess.domain.chess.Game;
import com.github.piotrostrow.chess.domain.chess.GameResult;
import com.github.piotrostrow.chess.ws.dto.Move;
import com.github.piotrostrow.chess.ws.dto.MoveResponse;
import org.springframework.scheduling.TaskScheduler;

import java.security.Principal;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.function.BiConsumer;

// TODO: encapsulate game, players
public class GameSession {

	private static final int DEFAULT_TIME_PER_PLAYER = 180;

	private final TaskScheduler taskScheduler;
	private final BiConsumer<GameSession, GameResult> onTimeOut;

	private final Player white;
	private final Player black;

	private final Game game;
	private final int time;

	private ScheduledFuture<?> scheduledFuture;

	private Player disconnected = null;

	private long lastMoveTime = System.currentTimeMillis();

	public GameSession(Principal white, Principal black, TaskScheduler taskScheduler, BiConsumer<GameSession, GameResult> onTimeOut) {
		this(white, black, taskScheduler, onTimeOut, DEFAULT_TIME_PER_PLAYER);
	}

	public GameSession(Principal white, Principal black, TaskScheduler taskScheduler, BiConsumer<GameSession, GameResult> onTimeOut, int time) {
		this(white, black, taskScheduler, onTimeOut, time, Fen.DEFAULT_STARTING_POSITION);
	}

	public GameSession(Principal white, Principal black, TaskScheduler taskScheduler, BiConsumer<GameSession, GameResult> onTimeOut, int time, Fen fen) {
		this(new Player(white, Color.WHITE, time * 1000L), new Player(black, Color.BLACK, time * 1000L), taskScheduler, onTimeOut, time, fen);
	}

	GameSession(Player white, Player black, TaskScheduler taskScheduler, BiConsumer<GameSession, GameResult> onTimeOut, int time, Fen fen) {
		this.taskScheduler = taskScheduler;
		this.onTimeOut = onTimeOut;
		this.white = white;
		this.black = black;
		this.game = new Game(fen);
		this.time = time;

		scheduleTimeout(white);
	}

	private void scheduleTimeout(Player player) {
		scheduledFuture = taskScheduler.schedule(() -> endGameByTimeout(player), Instant.now().plusMillis(player.getTime()));
	}

	private void endGameByTimeout(Player timedOutPlayer) {
		synchronized (game) {
			GameResult gameResult = game.getGameResult();
			if (gameResult == GameResult.ONGOING) {
				timedOutPlayer.setTime(0);
				onTimeOut.accept(this, GameResult.TIMEOUT);
			}
		}
	}

	public void disconnected(Principal principal) {
		synchronized (game) {
			// If task could not be cancelled - the game already ended by timeout
			if (scheduledFuture.cancel(false)) {
				disconnected = getPlayerByName(principal.getName());
			}
		}
	}

	public Optional<MoveResponse> move(Move move, Principal player) {
		Player currentPlayer = getPlayerByName(player.getName());
		Player opponent = getOpponent(currentPlayer);

		synchronized (game) {
			if (!moveIfLegal(move, currentPlayer)) {
				return Optional.empty();
			}

			// If it could not cancel, then the scheduled method is executing. Meaning that the last move happen to be
			// just in time. The timeout method takes care of checking the result - it'll call the callback iff the
			// game result == ONGOING.
			if (scheduledFuture.cancel(false) && game.getGameResult() == GameResult.ONGOING) {
				scheduleTimeout(opponent);
			}

			return Optional.of(new MoveResponse(move, white.getTime(), black.getTime()));
		}
	}

	private Player getPlayerByName(String name) {
		if (white.getName().equals(name)) {
			return white;
		} else if (black.getName().equals(name)) {
			return black;
		} else {
			throw new IllegalArgumentException();
		}
	}

	private Player getOpponent(Player currentPlayer) {
		if (white.getColor() == currentPlayer.getColor()) {
			return black;
		} else {
			return white;
		}
	}

	private boolean moveIfLegal(Move move, Player currentPlayer) {
		long currentTime = System.currentTimeMillis();

		boolean moved = disconnected == null &&
				!hasTimedOut(currentPlayer, currentTime) &&
				game.getActiveColor() == currentPlayer.getColor() &&
				game.moveIfLegal(move);

		if (moved) {
			currentPlayer.subtractTime(currentTime - lastMoveTime);
			lastMoveTime = currentTime;
		}

		return moved;
	}

	private boolean hasTimedOut(Player currentPlayer, long currentTime) {
		return currentTime - lastMoveTime >= currentPlayer.getTime();
	}

	public GameResult getGameResult() {
		if (disconnected != null) {
			return GameResult.DISCONNECTED;
		} else if (white.getTime() == 0 || black.getTime() == 0) {
			return GameResult.TIMEOUT;
		} else {
			return game.getGameResult();
		}
	}

	public Optional<Color> getWinner() {
		if (disconnected != null) {
			return Optional.of(getOpponent(disconnected).getColor());
		}

		GameResult gameResult = game.getGameResult();
		if (gameResult == GameResult.CHECKMATE) {
			return Optional.of(game.getWinner());
		} else if (gameResult.isDraw()) {
			return Optional.empty();
		} else if (white.getTime() == 0) {
			return Optional.of(Color.BLACK);
		} else if (black.getTime() == 0) {
			return Optional.of(Color.WHITE);
		} else {
			return Optional.empty();
		}
	}

	public Game getGame() {
		return game;
	}

	public Player getWhite() {
		return white;
	}

	public Player getBlack() {
		return black;
	}

	public int getTime() {
		return time;
	}
}
