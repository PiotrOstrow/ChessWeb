package com.github.piotrostrow.chess.domain.chess.pieces;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.Position;

import java.util.Map;
import java.util.Set;

public class Bishop extends Piece {

	public Bishop(Color color, Position position) {
		super(color, position);
	}

	@Override
	public Set<Position> getPseudoLegalMoves(Map<Position, Piece> pieces) {
		return getDiagonalMoves(pieces);
	}

	@Override
	public Piece moved(Position to) {
		return new Bishop(getColor(), to);
	}
}
