package com.github.piotrostrow.chess.rest.controller;

import com.github.piotrostrow.chess.rest.serivce.PuzzleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("puzzles")
public class PuzzleController {

	private final PuzzleService puzzleService;

	public PuzzleController(PuzzleService puzzleService) {
		this.puzzleService = puzzleService;
	}

	@GetMapping
	public ResponseEntity<Object> get() {
		return ResponseEntity.ok(Map.of("puzzles", puzzleService.getAllPuzzles()));
	}

	@GetMapping("random")
	public ResponseEntity<Object> randomPuzzle() {
		return ResponseEntity.ok(puzzleService.getRandomPuzzle());
	}
}
