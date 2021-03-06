package com.github.piotrostrow.chess.domain.chess;

import com.github.piotrostrow.chess.domain.chess.pieces.*;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.github.piotrostrow.chess.domain.chess.Color.BLACK;
import static com.github.piotrostrow.chess.domain.chess.Color.WHITE;

/**
 * https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation
 */
public class Fen {

	private static final Pattern PATTERN = Pattern.compile("((?<empty>\\d)|(?<piece>[kqrnbpKQRNBP]))", Pattern.MULTILINE);

	public static final Fen DEFAULT_STARTING_POSITION = new Fen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

	private final String fenString;

	private List<Piece> pieces;
	private Color activeColor;
	private Set<CastlingMove> castlingAvailability;
	private Position enPassantTarget;

	public Fen(String fenString) {
		this.fenString = fenString;
		parseFen(fenString);
	}

	private void parseFen(String fenString) {
		String[] sections = fenString.split(" ");

		pieces = Collections.unmodifiableList(parseRanks(sections[0]));
		activeColor = parseActiveColor(sections[1]);
		castlingAvailability = Collections.unmodifiableSet(parseCastlingAvailability(sections[2]));
		enPassantTarget = parseEnPassantTarget(sections[3]);
	}

	private List<Piece> parseRanks(String ranks) {
		List<Piece> result = new ArrayList<>();

		int rank = 7;
		for (String rankString : ranks.split("/")) {
			result.addAll(parseRank(rank, rankString));
			--rank;
		}
		return result;
	}

	private List<Piece> parseRank(int rank, String rankString) {
		List<Piece> result = new ArrayList<>();
		Matcher matcher = PATTERN.matcher(rankString);
		int file = 0;

		while (matcher.find()) {
			String emptySquares = matcher.group("empty");
			if (StringUtils.hasLength(emptySquares)) {
				file += Integer.parseInt(emptySquares);
			} else {
				String piece = matcher.group("piece");
				result.add(parsePiece(piece, file++, rank));
			}
		}

		return result;
	}

	private Piece parsePiece(String piece, int file, int rank) {
		switch (piece) {
			case "K":
				return new King(WHITE, new Position(file, rank));
			case "k":
				return new King(BLACK, new Position(file, rank));
			case "Q":
				return new Queen(WHITE, new Position(file, rank));
			case "q":
				return new Queen(BLACK, new Position(file, rank));
			case "B":
				return new Bishop(WHITE, new Position(file, rank));
			case "b":
				return new Bishop(BLACK, new Position(file, rank));
			case "N":
				return new Knight(WHITE, new Position(file, rank));
			case "n":
				return new Knight(BLACK, new Position(file, rank));
			case "R":
				return new Rook(WHITE, new Position(file, rank));
			case "r":
				return new Rook(BLACK, new Position(file, rank));
			case "P":
				return new Pawn(WHITE, new Position(file, rank));
			case "p":
				return new Pawn(BLACK, new Position(file, rank));
			default:
				throw new IllegalStateException("");
		}
	}

	private Color parseActiveColor(String section) {
		return section.equalsIgnoreCase("w") ? WHITE : BLACK;
	}

	private Set<CastlingMove> parseCastlingAvailability(String section) {
		return Arrays.stream(CastlingMove.values())
				.filter(e -> section.contains(String.valueOf(e.value())))
				.collect(Collectors.toSet());
	}

	private Position parseEnPassantTarget(String section) {
		if (section.length() == 0 || section.length() > 2) {
			throw new IllegalArgumentException("Invalid FEN - error parsing en passant target square");
		}

		if (section.equals("-")) {
			return null;
		}

		return new Position(section);
	}

	public List<Piece> getPieces() {
		return pieces;
	}

	public Color getActiveColor() {
		return activeColor;
	}

	public Set<CastlingMove> getCastlingAvailability() {
		return castlingAvailability;
	}

	public Optional<Position> getEnPassantTarget() {
		return Optional.ofNullable(enPassantTarget);
	}

	public String asString() {
		return fenString;
	}
}
