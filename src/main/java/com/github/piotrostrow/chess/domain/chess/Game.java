package com.github.piotrostrow.chess.domain.chess;

import com.github.piotrostrow.chess.domain.User;
import com.github.piotrostrow.chess.domain.chess.pieces.Piece;
import com.github.piotrostrow.chess.ws.dto.Move;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Game {

	private final User white;
	private final User black;

	private final Map<Position, Piece> pieces;

	private final Map<Color, Set<Position>> controlledSquares = new HashMap<>();

	public Game(User white, User black) {
		this(white, black, Fen.DEFAULT_STARTING_POSITION);
	}

	public Game(User white, User black, Fen fen) {
		this.white = white;
		this.black = black;
		this.pieces = fen.getPieces().stream().collect(Collectors.toMap(Piece::getPosition, e -> e));

		calculateControlledSquares();
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
		Piece from = pieces.get(move.getFrom());
		Piece to = pieces.get(move.getTo());

		calculateControlledSquares();
		return true;
	}

	public User getWhite() {
		return white;
	}

	public User getBlack() {
		return black;
	}
}
