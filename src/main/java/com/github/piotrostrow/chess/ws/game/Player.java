package com.github.piotrostrow.chess.ws.game;

import com.github.piotrostrow.chess.domain.chess.Color;

import java.security.Principal;

public class Player {

	private final String name;
	private final Color color;

	private long time;

	public Player(Principal principal, Color color, long time) {
		this(principal.getName(), color, time);
	}

	public Player(String name, Color color, long time) {
		this.name = name;
		this.color = color;
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public Color getColor() {
		return color;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void subtractTime(long time) {
		this.time = Math.max(0, this.time - time);
	}
}
