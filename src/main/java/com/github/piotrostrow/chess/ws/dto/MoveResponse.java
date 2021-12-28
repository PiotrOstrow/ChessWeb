package com.github.piotrostrow.chess.ws.dto;

public class MoveResponse {

	private final Move move;
	private final long whiteTime;
	private final long blackTime;

	public MoveResponse(Move move, long whiteTime, long blackTime) {
		this.move = move;
		this.whiteTime = whiteTime;
		this.blackTime = blackTime;
	}

	public Move getMove() {
		return move;
	}

	public long getWhiteTime() {
		return whiteTime;
	}

	public long getBlackTime() {
		return blackTime;
	}
}
