package com.github.piotrostrow.chess.ws.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.piotrostrow.chess.domain.chess.Position;

public class Move {

	private final Position from;
	private final Position to;

	@JsonCreator
	public Move(String from, String to) {
		this.from = new Position((byte) from.charAt(0) - (byte) 'a', Integer.parseInt(from.substring(1)) - 1);
		this.to = new Position((byte) to.charAt(0) - (byte) 'a', Integer.parseInt(to.substring(1)) - 1);
	}

	public Position getFrom() {
		return from;
	}

	public Position getTo() {
		return to;
	}

	public boolean isValid() {
		return from.isValid() && to.isValid();
	}
}
