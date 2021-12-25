package com.github.piotrostrow.chess.ws.service;

import com.github.piotrostrow.chess.domain.chess.GameResult;
import com.github.piotrostrow.chess.rest.serivce.GameService;
import com.github.piotrostrow.chess.ws.dto.Move;
import com.github.piotrostrow.chess.ws.game.GameSession;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameManager {

	private final WebSocketService webSocketService;
	private final GameService gameService;

	private final Map<String, GameSession> gamesByUsername = new ConcurrentHashMap<>();

	public GameManager(WebSocketService webSocketService, GameService gameService) {
		this.webSocketService = webSocketService;
		this.gameService = gameService;
	}

	public void startGame(Principal white, Principal black) {
		GameSession game = new GameSession(white, black);

		synchronized (gamesByUsername) {
			gamesByUsername.put(white.getName(), game);
			gamesByUsername.put(black.getName(), game);
		}

		webSocketService.sendStartGame(white.getName(), black.getName());
	}

	public void move(Principal principal, Move move) {
		GameSession gameSession = gamesByUsername.get(principal.getName());

		if (gameSession == null || !gameSession.move(move, principal)) {
			return;
		}

		if (principal.getName().equals(gameSession.getWhite().getName())) {
			webSocketService.sendMove(gameSession.getBlack().getName(), move);
		} else {
			webSocketService.sendMove(gameSession.getWhite().getName(), move);
		}

		if (gameSession.getGameResult() != GameResult.ONGOING) {
			endGame(gameSession, gameSession.getGameResult());
		}
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

	// TODO event listener here
	public void disconnected(Principal user) {
		GameSession gameSession = gamesByUsername.get(user.getName());
		if (gameSession != null) {
			gamesByUsername.remove(gameSession.getWhite().getName());
			gamesByUsername.remove(gameSession.getBlack().getName());

			if (user.getName().equals(gameSession.getWhite().getName())) {
				webSocketService.sendGameOver(gameSession.getBlack().getName(), GameResult.DISCONNECTED);
			} else {
				webSocketService.sendGameOver(gameSession.getWhite().getName(), GameResult.DISCONNECTED);
			}
		}
	}
}
