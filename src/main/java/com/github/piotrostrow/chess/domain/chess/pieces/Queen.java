package com.github.piotrostrow.chess.domain.chess.pieces;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.Position;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Queen extends Piece {

	public Queen(Color color, Position position) {
		super(color, position);
	}

	@Override
	public Set<Position> getPseudoLegalMoves(Map<Position, Piece> pieces) {
		Set<Position> result = new HashSet<>();
		result.addAll(getDiagonalMoves(pieces));
		result.addAll(getRookMoves(pieces));
		return result;
	}

	@Override
	public Piece moved(Position to) {
		return new Queen(getColor(), to);
	}
}
