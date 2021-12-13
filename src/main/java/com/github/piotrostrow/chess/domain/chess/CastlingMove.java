package com.github.piotrostrow.chess.domain.chess;

import static com.github.piotrostrow.chess.domain.chess.Color.BLACK;
import static com.github.piotrostrow.chess.domain.chess.Color.WHITE;

public enum CastlingMove {
	WHITE_KING_SIDE('K', WHITE, Position.WHITE_ROOK_KING_SIDE),
	WHITE_QUEEN_SIDE('Q', WHITE, Position.WHITE_ROOK_QUEEN_SIDE),
	BLACK_KING_SIDE('k', BLACK, Position.BLACK_ROOK_KING_SIDE),
	BLACK_QUEEN_SIDE('q', BLACK, Position.BLACK_ROOK_QUEEN_SIDE);

	private final char value;
	private final Color color;
	private final Position rookPosition;

	CastlingMove(char value, Color color, Position rookPosition) {
		this.value = value;
		this.color = color;
		this.rookPosition = rookPosition;
	}

	public char value() {
		return value;
	}

	public Color getColor() {
		return color;
	}

	public Position getRookPosition() {
		return rookPosition;
	}

	public boolean isQueenSide() {
		return this == BLACK_QUEEN_SIDE || this == WHITE_QUEEN_SIDE;
	}

	public Position getKingPosition() {
		return new Position(4, getRank());
	}

	public Position getKingTargetPosition() {
		return new Position(isQueenSide() ? 2 : 6, getRank());
	}

	private int getRank() {
		return color == Color.WHITE ? 0 : 7;
	}
}
