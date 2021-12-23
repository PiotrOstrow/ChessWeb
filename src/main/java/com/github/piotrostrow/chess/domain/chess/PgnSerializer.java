package com.github.piotrostrow.chess.domain.chess;

import java.util.List;

public class PgnSerializer {

	static final int MAX_LINE_LENGTH = 80;
	static final char LINE_BREAK = '\n';
	static final boolean LIMIT_LINE_LENGTH_DEFAULT = false;

	private PgnSerializer() {
	}

	public static String serialize(Game game) {
		return serialize(game, LIMIT_LINE_LENGTH_DEFAULT);
	}

	public static String serialize(Game game, boolean limitLineLength) {
		StringBuilder stringBuilder = new StringBuilder();

		buildMoves(game, stringBuilder);
		appendGameResult(game, stringBuilder);

		if (limitLineLength) {
			breakIntoLines(stringBuilder);
		}

		return stringBuilder.toString();
	}

	private static void buildMoves(Game game, StringBuilder stringBuilder) {
		List<MoveNotation> moves = game.getMoves();

		for (int i = 0; i < moves.size(); i++) {
			if (i % 2 == 0) {
				stringBuilder.append((i + 2) / 2);
				stringBuilder.append(". ");
			}

			stringBuilder.append(moves.get(i).getNotation());
			stringBuilder.append(' ');
		}
	}

	private static void appendGameResult(Game game, StringBuilder stringBuilder) {
		if (game.getGameResult() == GameResult.ONGOING) {
			stringBuilder.append('*');
		} else if (game.getGameResult() != GameResult.CHECKMATE) {
			stringBuilder.append("1/2-1/2");
		} else {
			if (game.getWinner() == Color.WHITE) {
				stringBuilder.append("1-0");
			} else {
				stringBuilder.append("0-1");
			}
		}
	}

	private static void breakIntoLines(StringBuilder stringBuilder) {
		int currentLineLength = 0;
		int lastIndex = 0;
		int currentIndex;
		while ((currentIndex = stringBuilder.indexOf(" ", lastIndex + 1)) != -1) {
			currentLineLength += currentIndex - lastIndex;
			if (currentLineLength > MAX_LINE_LENGTH) {
				stringBuilder.setCharAt(lastIndex, LINE_BREAK);
				currentLineLength = currentIndex - lastIndex;
			}
			lastIndex = currentIndex;
		}
	}
}
