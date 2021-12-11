package com.github.piotrostrow.chess.domain.chess.pieces;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.Position;

import java.util.*;

public abstract class Piece {

	private final Color color;
	private Position position;

	public Piece(Color color, Position position) {
		this.color = color;
		this.position = position;
	}

	public Color getColor() {
		return color;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public abstract Collection<Position> getPseudoLegalMoves(Map<Position, Piece> pieces);

	protected List<Position> getDiagonalMoves(Map<Position, Piece> pieces) {
		List<Position> result = new ArrayList<>();

		result.addAll(getPositions(pieces, 1, 1));
		result.addAll(getPositions(pieces, -1, 1));
		result.addAll(getPositions(pieces, -1, -1));
		result.addAll(getPositions(pieces, 1, -1));

		return result;
	}

	protected List<Position> getRookMoves(Map<Position, Piece> pieces) {
		List<Position> result = new ArrayList<>();

		result.addAll(getPositions(pieces, 0, 1));
		result.addAll(getPositions(pieces, 0, -1));
		result.addAll(getPositions(pieces, 1, 0));
		result.addAll(getPositions(pieces, -1, 0));

		return result;
	}

	private List<Position> getPositions(Map<Position, Piece> pieces, int xDirection, int yDirection) {
		List<Position> result = new ArrayList<>();

		Position nextPosition = new Position(position.getX() + xDirection, position.getY() + yDirection);
		;
		while (nextPosition.isValid()) {
			Piece occupyingPiece = pieces.get(nextPosition);
			if (occupyingPiece != null) {
				if (occupyingPiece.getColor() != this.color) {
					result.add(nextPosition);
				}
				break;
			}
			result.add(nextPosition);
			nextPosition = new Position(nextPosition.getX() + xDirection, nextPosition.getY() + yDirection);
		}
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Piece piece = (Piece) o;
		return color == piece.color && Objects.equals(position, piece.position);
	}

	@Override
	public int hashCode() {
		return Objects.hash(color, position);
	}
}
