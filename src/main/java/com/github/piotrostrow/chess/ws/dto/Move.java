package com.github.piotrostrow.chess.ws.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.piotrostrow.chess.domain.chess.Position;

import java.util.Objects;

public class Move {

	private final Position from;
	private final Position to;

	public Move(Position from, Position to) {
		this.from = from;
		this.to = to;
	}

	@JsonCreator
	public Move(String from, String to) {
		this.from = new Position(from);
		this.to = new Position(to);
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

	public int deltaY() {
		return Math.abs(from.getY() - to.getY());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Move move = (Move) o;
		return Objects.equals(from, move.from) && Objects.equals(to, move.to);
	}

	@Override
	public int hashCode() {
		return Objects.hash(from, to);
	}

	@Override
	public String toString() {
		return "Move{" +
				"from=" + from +
				", to=" + to +
				'}';
	}
}
