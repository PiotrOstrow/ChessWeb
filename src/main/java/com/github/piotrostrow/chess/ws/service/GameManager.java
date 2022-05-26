package com.github.piotrostrow.chess.ws.service;

import com.github.piotrostrow.chess.domain.chess.GameResult;
import com.github.piotrostrow.chess.rest.serivce.GameService;
import com.github.piotrostrow.chess.ws.dto.Move;
import com.github.piotrostrow.chess.ws.game.GameSession;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class GameManager {

	private final WebSocketService webSocketService;
	private final GameService gameService;
	private final TaskScheduler taskScheduler;

	private final ConcurrentMap<String, GameSession> gamesByUsername = new ConcurrentHashMap<>();

	public GameManager(WebSocketService webSocketService, GameService gameService, ThreadPoolTaskScheduler taskScheduler) {
		this.webSocketService = webSocketService;
		this.gameService = gameService;
		this.taskScheduler = taskScheduler;
	}

	public void startGame(Principal white, Principal black) {
		GameSession gameSession;

		synchronized (gamesByUsername) {
			if (isPlaying(white) || isPlaying(black)) {
				return;
			}

			gameSession = new GameSession(white, black, taskScheduler, this::endGame);
			gamesByUsername.put(white.getName(), gameSession);
			gamesByUsername.put(black.getName(), gameSession);
		}

		webSocketService.sendStartGame(white.getName(), black.getName(), gameSession.getTime());
	}

	public void move(Principal principal, Move move) {
		GameSession gameSession = gamesByUsername.get(principal.getName());

		if (gameSession == null) {
			return;
		}

		gameSession.move(move, principal).ifPresent(moveResponse -> {
			webSocketService.sendMove(gameSession.getBlack().getName(), moveResponse);
			webSocketService.sendMove(gameSession.getWhite().getName(), moveResponse);

			GameResult gameResult = gameSession.getGameResult();
			if (gameResult != GameResult.ONGOING) {
				endGame(gameSession, gameResult);
			}
		});
	}

	private void endGame(GameSession gameSession, GameResult gameResult) {
		webSocketService.sendGameOver(gameSession.getWhite().getName(), gameResult);
		webSocketService.sendGameOver(gameSession.getBlack().getName(), gameResult);

		gamesByUsername.remove(gameSession.getWhite().getName());
		gamesByUsername.remove(gameSession.getBlack().getName());

		gameService.saveGame(gameSession);
	}

	public boolean isPlaying(Principal user) {
		synchronized (gamesByUsername) { // locking here and on inserts is enough
			return gamesByUsername.containsKey(user.getName());
		}
	}

	@EventListener
	public void handleSessionDisconnected(SessionDisconnectEvent event) {
		SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
		Principal principal = accessor.getUser();

		if (principal != null) {
			onDisconnect(principal);
		}
	}

	void onDisconnect(Principal principal) {
		GameSession gameSession = gamesByUsername.get(principal.getName());
		if (gameSession != null) {
			gameSession.disconnected(principal);
			gamesByUsername.remove(gameSession.getWhite().getName());
			gamesByUsername.remove(gameSession.getBlack().getName());

			if (principal.getName().equals(gameSession.getWhite().getName())) {
				webSocketService.sendGameOver(gameSession.getBlack().getName(), GameResult.DISCONNECTED);
			} else {
				webSocketService.sendGameOver(gameSession.getWhite().getName(), GameResult.DISCONNECTED);
			}

			gameService.saveGame(gameSession);
		}
	}
}
