package com.github.piotrostrow.chess.domain.chess.pieces;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.Position;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Queen extends Piece {

	public Queen(Color color, Position position) {
		super(color, position);
	}

	@Override
	public Collection<Position> getPseudoLegalMoves(Map<Position, Piece> pieces) {
		List<Position> result = new ArrayList<>();
		result.addAll(getDiagonalMoves(pieces));
		result.addAll(getRookMoves(pieces));
		return result;
	}
}
