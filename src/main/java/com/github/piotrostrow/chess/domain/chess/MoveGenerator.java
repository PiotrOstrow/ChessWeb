package com.github.piotrostrow.chess.domain.chess;

import com.github.piotrostrow.chess.domain.chess.pieces.King;
import com.github.piotrostrow.chess.domain.chess.pieces.Piece;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MoveGenerator {

	private MoveGenerator() {
	}

	public static Map<Position, Set<Position>> generateLegalMoves(Map<Position, Piece> pieces) {
		return pieces.values().stream()
				.collect(Collectors.toMap(
						Piece::getPosition,
						piece -> piece.getPseudoLegalMoves(pieces).stream()
								.filter(move -> isLegalMove(piece, move, new HashMap<>(pieces)))
								.collect(Collectors.toSet())
				));
	}

	private static boolean isLegalMove(Piece piece, Position move, Map<Position, Piece> pieces) {
		pieces.remove(piece.getPosition());
		pieces.put(move, piece.moved(move));

		Position kingPosition = pieces.values().stream()
				.filter(e -> e instanceof King && e.getColor() == piece.getColor())
				.map(Piece::getPosition)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("Missing king on the board"));

		return pieces.values().stream()
				.filter(e -> e.getColor() != piece.getColor())
				.flatMap(e -> e.getPseudoLegalMoves(pieces).stream())
				.noneMatch(e -> e.equals(kingPosition));
	}
}
