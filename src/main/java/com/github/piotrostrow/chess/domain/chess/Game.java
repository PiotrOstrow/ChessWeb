package com.github.piotrostrow.chess.domain.chess;

import com.github.piotrostrow.chess.domain.User;
import com.github.piotrostrow.chess.domain.chess.pieces.King;
import com.github.piotrostrow.chess.domain.chess.pieces.Pawn;
import com.github.piotrostrow.chess.domain.chess.pieces.Piece;
import com.github.piotrostrow.chess.domain.chess.pieces.Queen;
import com.github.piotrostrow.chess.ws.dto.Move;

import java.util.*;
import java.util.stream.Collectors;

public class Game {

	private final User white;
	private final User black;

	private final Map<Position, Piece> pieces;
	private final Map<Color, Set<Position>> controlledSquares = new EnumMap<>(Color.class);
	private Map<Position, Set<Position>> legalMoves;

	private Color activeColor;
	private final Set<CastlingMove> castlingAvailability;

	private Position enPassantTarget;

	public Game(User white, User black) {
		this(white, black, Fen.DEFAULT_STARTING_POSITION);
	}

	public Game(User white, User black, Fen fen) {
		this.white = white;
		this.black = black;
		this.pieces = fen.getPieces().stream().collect(Collectors.toMap(Piece::getPosition, e -> e));
		this.activeColor = fen.getActiveColor();
		this.castlingAvailability = new HashSet<>(fen.getCastlingAvailability());
		this.enPassantTarget = fen.getEnPassantTarget().orElse(null);

		calculateControlledSquares();
		generateLegalMoves();
	}

	private void generateLegalMoves() {
		legalMoves = MoveGenerator.generateLegalMoves(this);
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

	public synchronized boolean moveIfLegal(Move move, String playerName) {
		if (getActivePlayer().getName().equals(playerName)) {
			return moveIfLegal(move);
		}

		return false;
	}

	boolean moveIfLegal(Move move) {
		if (isMoveLegal(move)) {
			move(move);
			calculateControlledSquares();
			generateLegalMoves();
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
		if (isCastlingMove(move)) {
			handleCastlingMove(move);
		} else if (isEnPassantMove(move)) {
			handleEnPassantMove(move);
		} else {
			handleRegularMove(move);
		}

		updateCastlingAvailability();
		updateEnPassantTarget(move);

		activeColor = getNonActiveColor();
	}

	private boolean isCastlingMove(Move move) {
		return pieces.get(move.getFrom()) instanceof King && Math.abs(move.getFrom().getX() - move.getTo().getX()) == 2;
	}

	private boolean isEnPassantMove(Move move) {
		if (enPassantTarget == null) {
			return false;
		}

		return pieces.get(move.getFrom()) instanceof Pawn &&
				move.getFrom().getY() == enPassantTarget.getY() &&
				move.getTo().getX() == enPassantTarget.getX();
	}

	private void handleCastlingMove(Move move) {
		int rank = activeColor == Color.WHITE ? 0 : 7;
		boolean isKingSide = move.getTo().getX() == 6;

		Position rookPosition = new Position(isKingSide ? 7 : 0, rank);
		Position newRookPosition = new Position(isKingSide ? 5 : 3, rank);

		Piece king = pieces.get(move.getFrom());
		Piece rook = pieces.get(rookPosition);

		pieces.remove(rookPosition);
		pieces.remove(king.getPosition());

		pieces.put(move.getTo(), king.moved(move.getTo()));
		pieces.put(newRookPosition, rook.moved(newRookPosition));
	}

	private void handleEnPassantMove(Move move) {
		Piece moving = pieces.get(move.getFrom());

		pieces.remove(move.getFrom());
		pieces.remove(enPassantTarget);

		pieces.put(move.getTo(), moving.moved(move.getTo()));
	}

	private void handleRegularMove(Move move) {
		Piece movedPiece = pieces.get(move.getFrom());
		pieces.remove(move.getFrom());

		if (movedPiece instanceof Pawn && (move.getTo().getY() == 7 || move.getTo().getY() == 0)) {
			pieces.put(move.getTo(), new Queen(movedPiece.getColor(), move.getTo()));
		} else {
			pieces.put(move.getTo(), movedPiece.moved(move.getTo()));
		}
	}

	private void updateCastlingAvailability() {
		if (!pieces.containsKey(Position.WHITE_KING)) {
			castlingAvailability.removeIf(move -> move.getColor() == Color.WHITE);
		}

		if (!pieces.containsKey(Position.BLACK_KING)) {
			castlingAvailability.removeIf(move -> move.getColor() == Color.BLACK);
		}

		castlingAvailability.removeIf(move -> !pieces.containsKey(move.getRookPosition())
				|| pieces.get(move.getRookPosition()).getColor() != move.getColor());
	}

	private void updateEnPassantTarget(Move lastMove) {
		Piece piece = pieces.get(lastMove.getTo());
		if (piece instanceof Pawn && lastMove.deltaY() == 2) {
			enPassantTarget = lastMove.getTo();
		} else {
			enPassantTarget = null;
		}
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

	Color getActiveColor() {
		return activeColor;
	}

	Color getNonActiveColor() {
		return activeColor == Color.WHITE ? Color.BLACK : Color.WHITE;
	}

	public User getWhite() {
		return white;
	}

	public User getBlack() {
		return black;
	}

	Map<Position, Piece> getPieces() {
		return Collections.unmodifiableMap(pieces);
	}

	boolean canCastle(CastlingMove castlingMove) {
		return this.castlingAvailability.contains(castlingMove);
	}

	Optional<Position> getEnPassantTarget() {
		return Optional.ofNullable(enPassantTarget);
	}

	Set<Position> getControlledSquares(Color color) {
		return Collections.unmodifiableSet(controlledSquares.get(color));
	}

	private User getActivePlayer() {
		return activeColor == Color.WHITE ? white : black;
	}
}
