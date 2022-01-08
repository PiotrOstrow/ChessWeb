package com.github.piotrostrow.chess.entity;

import javax.persistence.*;

@Entity
public class PuzzleDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToOne
	private PuzzleEntity puzzleEntity;

	private String fen;

	private String moves;

	public PuzzleDetailsEntity() {
	}

	public PuzzleDetailsEntity(String fen, String moves) {
		this.fen = fen;
		this.moves = moves;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PuzzleEntity getPuzzleEntity() {
		return puzzleEntity;
	}

	public void setPuzzleEntity(PuzzleEntity puzzleEntity) {
		this.puzzleEntity = puzzleEntity;
	}

	public String getFen() {
		return fen;
	}

	public void setFen(String fen) {
		this.fen = fen;
	}

	public String getMoves() {
		return moves;
	}

	public void setMoves(String moves) {
		this.moves = moves;
	}
}
