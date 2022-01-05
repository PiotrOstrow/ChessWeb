package com.github.piotrostrow.chess.rest.controller;

import com.github.piotrostrow.chess.rest.dto.PuzzleSolutionDto;
import com.github.piotrostrow.chess.rest.dto.PuzzleSolutionResponse;
import com.github.piotrostrow.chess.rest.serivce.PuzzleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
	public ResponseEntity<Object> randomPuzzle(Principal principal) {
		return ResponseEntity.ok(puzzleService.getRandomPuzzle(principal));
	}

	@PostMapping("solve")
	public ResponseEntity<Object> submitSolution(@RequestBody PuzzleSolutionDto puzzleSolutionDto, Principal principal) {
		PuzzleSolutionResponse puzzleSolutionResponse = puzzleService.submitSolution(puzzleSolutionDto, principal);
		return ResponseEntity.ok(puzzleSolutionResponse);
	}
}
