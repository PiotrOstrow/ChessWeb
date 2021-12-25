package com.github.piotrostrow.chess.ws;

import com.github.piotrostrow.chess.domain.User;
import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.Game;
import com.github.piotrostrow.chess.domain.chess.GameResult;
import com.github.piotrostrow.chess.ws.dto.Move;

import java.security.Principal;

public class GameSession {

	private final User white;
	private final User black;

	private final Game game;

	public GameSession(Principal white, Principal black) {
		this.white = new User(white.getName(), Color.WHITE);
		this.black = new User(black.getName(), Color.BLACK);

		this.game = new Game();
	}

	public boolean move(Move move, Principal player) {
		User user = getPlayerByName(player.getName());

		synchronized (game) {
			if (game.getActiveColor() == user.getColor()) {
				return game.moveIfLegal(move);
			}
		}

		return false;
	}

	private User getPlayerByName(String name) {
		if (white.getName().equals(name)) {
			return white;
		} else if (black.getName().equals(name)) {
			return black;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public GameResult getGameResult() {
		synchronized (game) {
			return game.getGameResult();
		}
	}

	public Game getGame() {
		return game;
	}

	public User getWhite() {
		return white;
	}

	public User getBlack() {
		return black;
	}
}
