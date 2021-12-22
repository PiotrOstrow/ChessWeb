package com.github.piotrostrow.chess.domain.chess;

import com.github.piotrostrow.chess.domain.chess.pieces.Piece;
import com.github.piotrostrow.chess.ws.dto.Move;

import java.util.Objects;

public class MoveNotation {

	public static final MoveNotation CASTLE_KING_SIDE = new MoveNotation("O-O");
	public static final MoveNotation CASTLE_QUEEN_SIDE = new MoveNotation("O-O-O");

	private final String notation;

	private MoveNotation(String notation) {
		this.notation = notation;
	}

	public static MoveNotation pawnCapture(Move move) {
		return new MoveNotation(move.getFrom().getFile() + "x" + move.getTo().getNotation());
	}

	public static MoveNotation move(Piece moved, String ambiguityNotation, boolean captured, Position to) {
		return new MoveNotation(moved.getPieceLetter() + ambiguityNotation + (captured ? 'x' : "") + to.getNotation());
	}

	public MoveNotation withCheck() {
		return new MoveNotation(notation + '+');
	}

	public MoveNotation withCheckMate() {
		return new MoveNotation(notation + '#');
	}

	public MoveNotation withPromote(Piece piece) {
		return withPromote(piece.getPieceLetter());
	}

	public MoveNotation withPromote(String piece) {
		return new MoveNotation(notation + '=' + piece);
	}

	public String getNotation() {
		return notation;
	}

	@Override
	public String toString() {
		return notation;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MoveNotation that = (MoveNotation) o;
		return Objects.equals(notation, that.notation);
	}

	@Override
	public int hashCode() {
		return Objects.hash(notation);
	}
}
