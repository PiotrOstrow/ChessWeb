package com.github.piotrostrow.chess.domain.chess.pieces;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.Position;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Knight extends Piece {

	public Knight(Color color, Position position) {
		super(color, position);
	}

	@Override
	public String getPieceLetter() {
		return "N";
	}

	@Override
	public Set<Position> getPseudoLegalMoves(Map<Position, Piece> pieces) {
		return Stream.of(
						new Position(getPosition().getX() + 2, getPosition().getY() + 1),
						new Position(getPosition().getX() - 2, getPosition().getY() + 1),
						new Position(getPosition().getX() - 2, getPosition().getY() - 1),
						new Position(getPosition().getX() + 2, getPosition().getY() - 1),
						new Position(getPosition().getX() + 1, getPosition().getY() + 2),
						new Position(getPosition().getX() - 1, getPosition().getY() + 2),
						new Position(getPosition().getX() - 1, getPosition().getY() - 2),
						new Position(getPosition().getX() + 1, getPosition().getY() - 2)
				)
				.filter(Position::isValid)
				.filter(e -> !pieces.containsKey(e) || pieces.get(e).getColor() != this.getColor())
				.collect(Collectors.toSet());
	}

	@Override
	public Piece moved(Position to) {
		return new Knight(getColor(), to);
	}
}
