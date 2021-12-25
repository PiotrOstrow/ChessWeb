package com.github.piotrostrow.chess.domain;

import com.github.piotrostrow.chess.domain.chess.Color;

public class User { // TODO: rename to player

	private final String name;
	private final Color color;

	public User(String name, Color color) {
		this.name = name;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public Color getColor() {
		return color;
	}
}
