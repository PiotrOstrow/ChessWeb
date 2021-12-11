package com.github.piotrostrow.chess.ws;

import com.github.piotrostrow.chess.domain.User;
import com.github.piotrostrow.chess.domain.chess.Game;
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
		if(game.moveIfValid(move)) {
			if(principal.getName().equals(game.getWhite().getName())){
				webSocketService.sendMove(game.getBlack().getName(), move);
			} else {
				webSocketService.sendMove(game.getWhite().getName(), move);
			}
		}
	}

	public boolean isPlaying(User user) {
		return gameByUsername.containsKey(user.getName());
	}
}