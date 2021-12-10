package com.github.piotrostrow.chess.domain.chess;

import com.github.piotrostrow.chess.domain.User;
import com.github.piotrostrow.chess.ws.dto.Move;

public class Game {

	private final User white;
	private final User black;

	public Game(User white, User black) {
		this.white = white;
		this.black = black;
	}

	public boolean moveIfValid(Move move) {
		return true;
	}

	public User getWhite() {
		return white;
	}

	public User getBlack() {
		return black;
	}
}
