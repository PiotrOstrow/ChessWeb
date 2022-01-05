package com.github.piotrostrow.chess.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class PuzzleThemeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true)
	private String name;

	@ManyToMany(mappedBy = "themes", fetch = FetchType.LAZY)
	private Set<PuzzleEntity> puzzles = new HashSet<>();

	public PuzzleThemeEntity() {
	}

	public PuzzleThemeEntity(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<PuzzleEntity> getPuzzles() {
		return puzzles;
	}

	public void setPuzzles(Set<PuzzleEntity> puzzles) {
		this.puzzles = puzzles;
	}
}
