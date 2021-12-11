package com.github.piotrostrow.chess.domain.chess.pieces;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.Position;

import java.util.Collection;
import java.util.Map;

public class Rook extends Piece {
	public Rook(Color color, Position position) {
		super(color, position);
	}

	@Override
	public Collection<Position> getPseudoLegalMoves(Map<Position, Piece> pieces) {
		return getRookMoves(pieces);
	}
}
