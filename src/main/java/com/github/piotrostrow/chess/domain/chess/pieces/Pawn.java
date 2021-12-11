package com.github.piotrostrow.chess.domain.chess.pieces;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.Position;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Pawn extends Piece {

	public Pawn(Color color, Position position) {
		super(color, position);
	}

	@Override
	public Collection<Position> getPseudoLegalMoves(Map<Position, Piece> pieces) {
		List<Position> result = new ArrayList<>();

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
}
