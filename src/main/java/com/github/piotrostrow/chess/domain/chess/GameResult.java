package com.github.piotrostrow.chess.domain.chess;

public enum GameResult {
	// TODO: implement draw by various conditions
	ONGOING, CHECKMATE, STALEMATE, DRAW, DISCONNECTED, TIMEOUT;

	public boolean isDraw() {
		return this == STALEMATE || this == DRAW;
	}
}
