package com.github.piotrostrow.chess.entity;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.GameResult;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
public class GameEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToMany(mappedBy = "game", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<GamePlayedEntity> gamesPlayed = new HashSet<>();

	@Column(length = 4096)
	private String pgn;

	private GameResult gameResult;

	private Color winner;

	@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
	private Timestamp timestamp;

	public GameEntity() {
	}

	public GameEntity(Long id, Set<GamePlayedEntity> gamesPlayed, String pgn, GameResult gameResult, Color winner, Timestamp timestamp) {
		this.id = id;
		this.gamesPlayed = gamesPlayed;
		this.pgn = pgn;
		this.gameResult = gameResult;
		this.winner = winner;
		this.timestamp = timestamp;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPgn() {
		return pgn;
	}

	public void setPgn(String pgn) {
		this.pgn = pgn;
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

	public Set<GamePlayedEntity> getGamesPlayed() {
		return gamesPlayed;
	}

	public void setGamesPlayed(Set<GamePlayedEntity> gameRecords) {
		this.gamesPlayed = gameRecords;
	}

	public void addGameRecord(GamePlayedEntity gamePlayedEntity) {
		this.gamesPlayed.add(gamePlayedEntity);
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
}
