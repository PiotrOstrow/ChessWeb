package com.github.piotrostrow.chess.domain.chess.pieces;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.Position;

import java.util.Map;
import java.util.Set;

public class Rook extends Piece {

	public Rook(Color color, Position position) {
		super(color, position);
	}

	@Override
	public String getPieceLetter() {
		return "R";
	}

	@Override
	public Set<Position> getPseudoLegalMoves(Map<Position, Piece> pieces) {
		return getRookMoves(pieces);
	}

	@Override
	public Piece moved(Position to) {
		return new Rook(getColor(), to);
	}
}
