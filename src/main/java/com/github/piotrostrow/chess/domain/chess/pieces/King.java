package com.github.piotrostrow.chess.domain.chess.pieces;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.Position;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class King extends Piece {

	public King(Color color, Position position) {
		super(color, position);
	}

	@Override
	public String getPieceLetter() {
		return "K";
	}

	@Override
	public Set<Position> getPseudoLegalMoves(Map<Position, Piece> pieces) {
		Set<Position> result = new HashSet<>();

		Position pos = getPosition();
		for (int x = Math.max(0, pos.getX() - 1); x <= Math.min(7, pos.getX() + 1); x++) {
			for (int y = Math.max(0, pos.getY() - 1); y <= Math.min(7, pos.getY() + 1); y++) {
				Position position = new Position(x, y);
				Piece occupiedBy = pieces.get(position);
				if ((x != pos.getX() || y != pos.getY()) && (occupiedBy == null || occupiedBy.getColor() != this.getColor())) {
					result.add(position);
				}
			}
		}

		return result;
	}

	@Override
	public Piece moved(Position to) {
		return new King(getColor(), to);
	}
}
