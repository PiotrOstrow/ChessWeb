package com.github.piotrostrow.chess.domain.chess;

import com.github.piotrostrow.chess.domain.chess.pieces.King;
import com.github.piotrostrow.chess.domain.chess.pieces.Pawn;
import com.github.piotrostrow.chess.domain.chess.pieces.Piece;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MoveGenerator {

	private MoveGenerator() {
	}

	// TODO: only generate moves for active player
	static Map<Position, Set<Position>> generateLegalMoves(Game game) {
		Map<Position, Piece> pieces = game.getPieces();
		Set<Position> controlledSquares = game.getControlledSquares(game.getNonActiveColor());

		Map<Position, Set<Position>> legalMoves = pieces.values().stream()
				.collect(Collectors.toMap(
						Piece::getPosition,
						piece -> piece.getPseudoLegalMoves(pieces).stream()
								.filter(move -> isLegalMove(piece, move, new HashMap<>(pieces)))
								.collect(Collectors.toSet())
				));

		Arrays.stream(CastlingMove.values())
				.filter(game::canCastle)
				.filter(e -> canCastle(pieces, controlledSquares, e))
				.forEach(e -> legalMoves.get(e.getKingPosition()).add(e.getKingTargetPosition()));

		if (game.getEnPassantTarget().isPresent()) {
			Position enPassantPosition = game.getEnPassantTarget().orElseThrow(IllegalStateException::new);
			int direction = game.getActiveColor() == Color.WHITE ? 1 : -1;
			Position captureTargetPosition = enPassantPosition.plusY(direction);

			Piece left = pieces.get(enPassantPosition.plusX(-1));
			if (left instanceof Pawn && left.getColor() == game.getActiveColor()) {
				legalMoves.get(left.getPosition()).add(captureTargetPosition);
			}

			Piece right = pieces.get(enPassantPosition.plusX(1));
			if (right instanceof Pawn && right.getColor() == game.getActiveColor()) {
				legalMoves.get(right.getPosition()).add(captureTargetPosition);
			}
		}

		return legalMoves;
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

	private static boolean canCastle(Map<Position, Piece> pieces, Set<Position> controlledSquares, CastlingMove move) {
		Position kingPosition = move.getKingPosition();

		if (controlledSquares.contains(kingPosition)) {
			return false;
		}

		int kingDirection = move.getRookPosition().getX() == 7 ? 1 : -1;

		for (int x = kingPosition.getX() + kingDirection; x != kingPosition.getX() + 3 * kingDirection; x += kingDirection) {
			Position p = new Position(x, kingPosition.getY());
			if (controlledSquares.contains(p) || pieces.containsKey(p))
				return false;
		}

		return !(move.isQueenSide() && pieces.containsKey(new Position(1, kingPosition.getY())));
	}
}
