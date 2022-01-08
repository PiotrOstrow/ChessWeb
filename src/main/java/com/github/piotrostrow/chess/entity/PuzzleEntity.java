package com.github.piotrostrow.chess.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
public class PuzzleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@NotNull
	private PuzzleDetailsEntity puzzleDetails = new PuzzleDetailsEntity();

	@ManyToMany(fetch = FetchType.EAGER)
	private Set<PuzzleThemeEntity> themes = new HashSet<>();

	private int rating;

	public PuzzleEntity() {
	}

	public PuzzleEntity(PuzzleDetailsEntity puzzleDetails, Set<PuzzleThemeEntity> themes, int rating) {
		this.puzzleDetails = puzzleDetails;
		this.themes = themes;
		this.rating = rating;
	}

	public PuzzleEntity(long id, PuzzleDetailsEntity puzzleDetails, Set<PuzzleThemeEntity> themes, int rating) {
		this.id = id;
		this.puzzleDetails = puzzleDetails;
		this.rating = rating;
		this.themes = themes;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public Set<PuzzleThemeEntity> getThemes() {
		return themes;
	}

	public String getFen() {
		return puzzleDetails.getFen();
	}

	public String getMoves() {
		return puzzleDetails.getMoves();
	}

	public void setThemes(Set<PuzzleThemeEntity> themes) {
		this.themes = themes;
	}

	public PuzzleDetailsEntity getPuzzleDetails() {
		return puzzleDetails;
	}

	public void setPuzzleDetails(PuzzleDetailsEntity puzzleDetails) {
		this.puzzleDetails = puzzleDetails;
	}
}
