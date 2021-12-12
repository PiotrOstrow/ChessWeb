package com.github.piotrostrow.chess.domain.chess;

import com.github.piotrostrow.chess.domain.User;
import com.github.piotrostrow.chess.domain.chess.pieces.King;
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
	private Map<Position, Set<Position>> legalMoves;

	private Color activeColor;

	public Game(User white, User black) {
		this(white, black, Fen.DEFAULT_STARTING_POSITION);
	}

	public Game(User white, User black, Fen fen) {
		this.white = white;
		this.black = black;
		this.pieces = fen.getPieces().stream().collect(Collectors.toMap(Piece::getPosition, e -> e));
		this.activeColor = fen.getActiveColor();

		generateLegalMoves();
		calculateControlledSquares();
	}

	private void generateLegalMoves() {
		legalMoves = MoveGenerator.generateLegalMoves(pieces);
	}

	private void calculateControlledSquares() {
		Set<Position> whitesControlledSquares = pieces.values().stream()
				.filter(e -> e.getColor() == Color.WHITE)
				.flatMap(e -> e.getPseudoLegalMoves(pieces).stream())
				.collect(Collectors.toSet());

		Set<Position> blacksControlledSquares = pieces.values().stream()
				.filter(e -> e.getColor() == Color.BLACK)
				.flatMap(e -> e.getPseudoLegalMoves(pieces).stream())
				.collect(Collectors.toSet());

		controlledSquares.put(Color.WHITE, whitesControlledSquares);
		controlledSquares.put(Color.BLACK, blacksControlledSquares);
	}

	public synchronized GameResult getGameResult() {
		boolean activeColorHasLegalMoves = pieces.values().stream()
				.filter(e -> e.getColor() == activeColor)
				.flatMap(e -> legalMoves.get(e.getPosition()).stream())
				.findAny()
				.isPresent();

		if (activeColorHasLegalMoves) {
			return GameResult.NONE;
		}

		if (isActiveColorInCheck()) {
			return GameResult.CHECKMATE;
		} else {
			return GameResult.STALEMATE;
		}
	}

	private boolean isActiveColorInCheck() {
		Piece king = pieces.values().stream()
				.filter(e -> e instanceof King && e.getColor() == activeColor)
				.findFirst().orElseThrow();

		return controlledSquares.get(getNonActiveColor()).contains(king.getPosition());
	}

	public synchronized boolean moveIfLegal(Move move) {
		if (isMoveLegal(move)) {
			move(move);
			generateLegalMoves();
			calculateControlledSquares();
			return true;
		}

		return false;
	}

	private boolean isMoveLegal(Move move) {
		Piece movedPiece = pieces.get(move.getFrom());
		if (movedPiece == null || movedPiece.getColor() != this.activeColor) {
			return false;
		}
		return legalMoves.get(move.getFrom()).contains(move.getTo());
	}

	private void move(Move move) {
		activeColor = getNonActiveColor();

		Piece movedPiece = pieces.get(move.getFrom());
		pieces.put(move.getTo(), movedPiece.moved(move.getTo()));
		pieces.remove(move.getFrom());
	}

	private Color getNonActiveColor() {
		return activeColor == Color.WHITE ? Color.BLACK : Color.WHITE;
	}

	public User getWhite() {
		return white;
	}

	public User getBlack() {
		return black;
	}
}
