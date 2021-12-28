package com.github.piotrostrow.chess.ws.dto;

import com.github.piotrostrow.chess.domain.chess.Color;

public class GameStart {

	private final Color color;
	private final String opponent;
	private final int time;

	public GameStart(Color color, String opponent, int time) {
		this.color = color;
		this.opponent = opponent;
		this.time = time;
	}

	public Color getColor() {
		return color;
	}

	public String getOpponent() {
		return opponent;
	}

	public int getTime() {
		return time;
	}
}
