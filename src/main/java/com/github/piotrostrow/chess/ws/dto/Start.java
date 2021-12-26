package com.github.piotrostrow.chess.ws.dto;

import com.github.piotrostrow.chess.domain.chess.Color;

public class Start {

	private final Color color;
	private final String opponent;

	public Start(Color color, String opponent) {
		this.color = color;
		this.opponent = opponent;
	}

	public Color getColor() {
		return color;
	}

	public String getOpponent() {
		return opponent;
	}
}
