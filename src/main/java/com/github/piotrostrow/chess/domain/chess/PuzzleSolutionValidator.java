package com.github.piotrostrow.chess.domain.chess;

import com.github.piotrostrow.chess.entity.PuzzleEntity;
import com.github.piotrostrow.chess.ws.dto.Move;

import java.util.List;

public class PuzzleSolutionValidator {

	private PuzzleSolutionValidator() {
	}

	/**
	 * @param moves List of strings in valid UCI format
	 */
	public static boolean isCorrectSolution(PuzzleEntity puzzleEntity, List<String> moves) {
		if (puzzleEntity.getMoves().equals(String.join(" ", moves))) {
			return true;
		}

		return allEqualExceptLastMove(puzzleEntity, moves) && movesEvaluateToCheckMate(new Fen(puzzleEntity.getFen()), moves);
	}

	private static boolean allEqualExceptLastMove(PuzzleEntity puzzleEntity, List<String> moves) {
		List<String> correctMoves = List.of(puzzleEntity.getMoves().split(" "));
		List<String> correctMovesExceptLast = correctMoves.subList(0, correctMoves.size() - 1);

		List<String> submittedMovesExceptLast = moves.subList(0, moves.size() - 1);

		return submittedMovesExceptLast.equals(correctMovesExceptLast);
	}

	private static boolean movesEvaluateToCheckMate(Fen fen, List<String> moves) {
		Game game = new Game(fen);
		return moves.stream().map(Move::new).allMatch(game::moveIfLegal) && game.getGameResult() == GameResult.CHECKMATE;
	}
}
