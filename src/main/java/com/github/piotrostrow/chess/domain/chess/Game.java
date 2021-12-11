package com.github.piotrostrow.chess.domain.chess;

import com.github.piotrostrow.chess.domain.User;
import com.github.piotrostrow.chess.domain.chess.pieces.Piece;
import com.github.piotrostrow.chess.ws.dto.Move;

import java.util.*;
import java.util.stream.Collectors;

public class Game {

	private final User white;
	private final User black;

	private final Map<Position, Piece> pieces;
	private final Map<Position, Set<Position>> pseudoLegalMoves = new HashMap<>();

	private final Map<Color, Set<Position>> controlledSquares = new EnumMap<>(Color.class);

	public Game(User white, User black) {
		this(white, black, Fen.DEFAULT_STARTING_POSITION);
	}

	public Game(User white, User black, Fen fen) {
		this.white = white;
		this.black = black;
		this.pieces = fen.getPieces().stream().collect(Collectors.toMap(Piece::getPosition, e -> e));

		calculatePseudoLegalMoves();
		calculateControlledSquares();
	}

	private void calculatePseudoLegalMoves() {
		Map<Position, Set<Position>> newPseudoLegalMoves = pieces.values().stream()
				.collect(Collectors.toMap(Piece::getPosition, e -> e.getPseudoLegalMoves(pieces)));

		pseudoLegalMoves.clear();
		pseudoLegalMoves.putAll(newPseudoLegalMoves);
	}

	private void calculateControlledSquares() {
		Set<Position> whitesControlledSquares = pieces.values().stream()
				.filter(e -> e.getColor() == Color.WHITE)
				.map(Piece::getPosition)
				.collect(Collectors.toSet());

		Set<Position> blacksControlledSquares = pieces.values().stream()
				.filter(e -> e.getColor() == Color.WHITE)
				.map(Piece::getPosition)
				.collect(Collectors.toSet());

		controlledSquares.put(Color.WHITE, whitesControlledSquares);
		controlledSquares.put(Color.BLACK, blacksControlledSquares);
	}

	public synchronized boolean moveIfValid(Move move) {
		if (!isLegalMove(move)) {
			return false;
		}

		Piece piece = pieces.get(move.getFrom()).moved(move.getTo());

		pieces.remove(move.getFrom());
		pieces.put(piece.getPosition(), piece);

		calculatePseudoLegalMoves();
		calculateControlledSquares();
		return true;
	}

	private boolean isLegalMove(Move move) {
		Piece from = pieces.get(move.getFrom());

		if (!move.isValid() || from == null) {
			return false;
		}

		return this.pseudoLegalMoves.getOrDefault(move.getFrom(), Collections.emptySet()).contains(move.getTo());
	}

	public User getWhite() {
		return white;
	}

	public User getBlack() {
		return black;
	}
}
