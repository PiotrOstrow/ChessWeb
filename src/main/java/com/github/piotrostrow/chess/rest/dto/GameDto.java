package com.github.piotrostrow.chess.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.GameResult;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public class GameDto {

	private String pgn;

	private String white;
	private String black;

	private GameResult gameResult;
	private Color winner;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss.SSS")
	private LocalDateTime timestamp;

	public String getPgn() {
		return pgn;
	}

	public void setPgn(String pgn) {
		this.pgn = pgn;
	}

	public String getWhite() {
		return white;
	}

	public void setWhite(String white) {
		this.white = white;
	}

	public String getBlack() {
		return black;
	}

	public void setBlack(String black) {
		this.black = black;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public GameResult getGameResult() {
		return gameResult;
	}

	public void setGameResult(GameResult gameResult) {
		this.gameResult = gameResult;
	}

	public Color getWinner() {
		return winner;
	}

	public void setWinner(Color winner) {
		this.winner = winner;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GameDto gameDto = (GameDto) o;
		return Objects.equals(pgn, gameDto.pgn) && Objects.equals(white, gameDto.white) && Objects.equals(black, gameDto.black) && gameResult == gameDto.gameResult && winner == gameDto.winner && Objects.equals(timestamp, gameDto.timestamp);
	}

	@Override
	public int hashCode() {
		return Objects.hash(pgn, white, black, gameResult, winner, timestamp);
	}
}
