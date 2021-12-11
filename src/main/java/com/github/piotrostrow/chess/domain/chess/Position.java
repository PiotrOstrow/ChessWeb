package com.github.piotrostrow.chess.domain.chess;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public class Position {

	private final int x;
	private final int y;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Position plus(int x, int y) {
		return new Position(this.x + x, this.y + y);
	}

	public Position plusY(int y) {
		return new Position(this.x, this.y + y);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isValid() {
		return x >= 0 && x < 8 && y >= 0 && y < 8;
	}

	@JsonValue
	public String getNotation() {
		char file = (char) (((byte) 'a') + x);
		return String.valueOf(file) + y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Position position = (Position) o;
		return x == position.x && y == position.y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public String toString() {
		return "Position{" +
				"x=" + x +
				", y=" + y +
				'}';
	}
}
