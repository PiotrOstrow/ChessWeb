package com.github.piotrostrow.chess.domain.chess;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public class Position {

	public static final Position WHITE_KING = new Position("e1");
	public static final Position BLACK_KING = new Position("e8");

	private final int x;
	private final int y;

	public Position(String notation) {
		this.x = (byte) notation.charAt(0) - (byte) 'a';
		this.y = Integer.parseInt(notation.substring(1)) - 1;
	}

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Position plus(int x, int y) {
		return new Position(this.x + x, this.y + y);
	}

	public Position plusX(int x) {
		return new Position(this.x + x, this.y);
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
		char file = getFile();
		return String.valueOf(file) + getRank();
	}

	public char getFile() {
		return (char) (((byte) 'a') + x);
	}

	public int getRank() {
		return (y + 1);
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
