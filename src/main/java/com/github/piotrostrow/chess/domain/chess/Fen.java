package com.github.piotrostrow.chess.domain.chess;

import com.github.piotrostrow.chess.domain.chess.pieces.*;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.piotrostrow.chess.domain.chess.Color.BLACK;
import static com.github.piotrostrow.chess.domain.chess.Color.WHITE;

/**
 * https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation
 * Just position for now
 * TODO more unit tests, only covers default starting position
 */
public class Fen {

	private static final Pattern PATTERN = Pattern.compile("(?<empty>\\d?)(?<piece>[kqrnbpKQRNBP])", Pattern.MULTILINE);

	public static final Fen DEFAULT_STARTING_POSITION = new Fen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

	private final List<Piece> pieces;

	public Fen(String fenString) {
		pieces = Collections.unmodifiableList(parseFen(fenString));
	}

	private List<Piece> parseFen(String fenString) {
		String ranks = fenString.split(" ")[0];
		return parseRanks(ranks);
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

	public List<Piece> getPieces() {
		return pieces;
	}

	public Color getActiveColor() {
		return WHITE;
	}
}
