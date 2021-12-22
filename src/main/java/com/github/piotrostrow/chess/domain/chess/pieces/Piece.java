package com.github.piotrostrow.chess.domain.chess.pieces;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.Position;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class Piece {

	private final Color color;
	private final Position position;

	protected Piece(Color color, Position position) {
		this.color = color;
		this.position = position;
	}

	public Color getColor() {
		return color;
	}

	public Position getPosition() {
		return position;
	}

	public abstract String getPieceLetter();

	public abstract Set<Position> getPseudoLegalMoves(Map<Position, Piece> pieces);

	public abstract Piece moved(Position to);

	protected Set<Position> getDiagonalMoves(Map<Position, Piece> pieces) {
		Set<Position> result = new HashSet<>();

		result.addAll(getPositions(pieces, 1, 1));
		result.addAll(getPositions(pieces, -1, 1));
		result.addAll(getPositions(pieces, -1, -1));
		result.addAll(getPositions(pieces, 1, -1));

		return result;
	}

	protected Set<Position> getRookMoves(Map<Position, Piece> pieces) {
		Set<Position> result = new HashSet<>();

		result.addAll(getPositions(pieces, 0, 1));
		result.addAll(getPositions(pieces, 0, -1));
		result.addAll(getPositions(pieces, 1, 0));
		result.addAll(getPositions(pieces, -1, 0));

		return result;
	}

	private Set<Position> getPositions(Map<Position, Piece> pieces, int xDirection, int yDirection) {
		Set<Position> result = new HashSet<>();

		Position nextPosition = new Position(position.getX() + xDirection, position.getY() + yDirection);

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
