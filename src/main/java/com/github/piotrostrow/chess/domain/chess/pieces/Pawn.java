package com.github.piotrostrow.chess.domain.chess.pieces;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.Position;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Pawn extends Piece {

	public Pawn(Color color, Position position) {
		super(color, position);
	}

	@Override
	public String getPieceLetter() {
		return "";
	}

	@Override
	public Set<Position> getPseudoLegalMoves(Map<Position, Piece> pieces) {
		Set<Position> result = new HashSet<>();

		int direction = getColor() == Color.WHITE ? 1 : -1;

		Position first = getPosition().plusY(direction);
		if (!pieces.containsKey(first)) {
			result.add(first);
		}

		if (hasNotMoved()) {
			Position second = getPosition().plusY(direction * 2);
			if (!pieces.containsKey(second)) {
				result.add(second);
			}
		}

		Position captureLeft = getPosition().plus(-1, direction);
		if (captureLeft.isValid() && pieces.getOrDefault(captureLeft, this).getColor() != getColor()) {
			result.add(captureLeft);
		}

		Position captureRight = getPosition().plus(1, direction);
		if (captureRight.isValid() && pieces.getOrDefault(captureRight, this).getColor() != getColor()) {
			result.add(captureRight);
		}

		return result;
	}

	private boolean hasNotMoved() {
		return getColor() == Color.WHITE ? getPosition().getY() == 1 : getPosition().getY() == 6;
	}

	@Override
	public Piece moved(Position to) {
		return new Pawn(getColor(), to);
	}
}
