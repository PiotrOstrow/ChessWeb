package com.github.piotrostrow.chess.ws.service;

import com.github.piotrostrow.chess.domain.User;
import com.github.piotrostrow.chess.domain.chess.Game;
import com.github.piotrostrow.chess.domain.chess.GameResult;
import com.github.piotrostrow.chess.ws.dto.Move;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameManager {

	private final WebSocketService webSocketService;

	private final Map<String, Game> gameByUsername = new ConcurrentHashMap<>();

	public GameManager(WebSocketService webSocketService) {
		this.webSocketService = webSocketService;
	}

	public void startGame(User white, User black) {
		Game game = new Game(white, black);

		gameByUsername.put(white.getName(), game);
		gameByUsername.put(black.getName(), game);

		webSocketService.sendStartGame(white.getName(), black.getName());
	}

	public void move(Principal principal, Move move) {
		Game game = gameByUsername.get(principal.getName());
		if (game != null && game.moveIfLegal(move, principal.getName())) {
			if (principal.getName().equals(game.getWhite().getName())) {
				webSocketService.sendMove(game.getBlack().getName(), move);
			} else {
				webSocketService.sendMove(game.getWhite().getName(), move);
			}

			GameResult gameResult = game.getGameResult();
			if (gameResult != GameResult.NONE) {
				endGame(game, gameResult);
			}
		}
	}

	private void endGame(Game game, GameResult gameResult) {
		webSocketService.sendGameOver(game.getWhite().getName(), gameResult);
		webSocketService.sendGameOver(game.getBlack().getName(), gameResult);

		gameByUsername.remove(game.getWhite().getName());
		gameByUsername.remove(game.getBlack().getName());
	}

	public boolean isPlaying(User user) {
		return gameByUsername.containsKey(user.getName());
	}

	public void disconnected(User user) {
		Game game = gameByUsername.get(user.getName());
		if (game != null) {
			gameByUsername.remove(game.getWhite().getName());
			gameByUsername.remove(game.getBlack().getName());

			if (user.getName().equals(game.getWhite().getName())) {
				webSocketService.sendGameOver(game.getBlack().getName(), GameResult.DISCONNECTED);
			} else {
				webSocketService.sendGameOver(game.getWhite().getName(), GameResult.DISCONNECTED);
			}
		}
	}
}
