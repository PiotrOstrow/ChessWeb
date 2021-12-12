package com.github.piotrostrow.chess.ws.dto;

import com.github.piotrostrow.chess.domain.chess.GameResult;

public class GameOver {

	private final GameResult gameResult;

	public GameOver(GameResult gameResult) {
		this.gameResult = gameResult;
	}

	public GameResult getGameResult() {
		return gameResult;
	}
}
