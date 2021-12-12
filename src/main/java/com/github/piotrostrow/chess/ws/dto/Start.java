package com.github.piotrostrow.chess.ws.dto;

import com.github.piotrostrow.chess.domain.chess.Color;

public class Start {

	private final Color color;

	public Start(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}
}
