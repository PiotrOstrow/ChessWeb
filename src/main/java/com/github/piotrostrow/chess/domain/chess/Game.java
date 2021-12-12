package com.github.piotrostrow.chess.domain.chess;

import com.github.piotrostrow.chess.domain.User;
import com.github.piotrostrow.chess.domain.chess.pieces.Piece;
import com.github.piotrostrow.chess.ws.dto.Move;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Game {

	private final User white;
	private final User black;

	private final Map<Position, Piece> pieces;
	private Map<Position, Set<Position>> legalMoves;

	public Game(User white, User black) {
		this(white, black, Fen.DEFAULT_STARTING_POSITION);
	}

	public Game(User white, User black, Fen fen) {
		this.white = white;
		this.black = black;
		this.pieces = fen.getPieces().stream().collect(Collectors.toMap(Piece::getPosition, e -> e));

		generateLegalMoves();
	}

	private void generateLegalMoves() {
		legalMoves = MoveGenerator.generateLegalMoves(pieces);
	}

	public synchronized boolean moveIfLegal(Move move) {
		if (isMoveLegal(move)) {
			move(move);
			generateLegalMoves();
			return true;
		}

		return false;
	}

	private boolean isMoveLegal(Move move) {
		return legalMoves.getOrDefault(move.getFrom(), Collections.emptySet()).contains(move.getTo());
	}

	private void move(Move move) {
		Piece movedPiece = pieces.get(move.getFrom());
		pieces.put(move.getTo(), movedPiece.moved(move.getTo()));
		pieces.remove(move.getFrom());
	}

	public User getWhite() {
		return white;
	}

	public User getBlack() {
		return black;
	}
}
