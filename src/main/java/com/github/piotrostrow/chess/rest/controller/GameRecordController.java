package com.github.piotrostrow.chess.rest.controller;

import com.github.piotrostrow.chess.rest.serivce.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("games")
public class GameRecordController {

	private final GameService gameRecordService;

	public GameRecordController(GameService gameRecordService) {
		this.gameRecordService = gameRecordService;
	}

	@GetMapping
	public ResponseEntity<Object> getAll() {
		return ResponseEntity.ok(gameRecordService.getGames());
	}

	@GetMapping("{username}")
	public ResponseEntity<Object> getForUser(@PathVariable String username) {
		return ResponseEntity.ok(gameRecordService.getGamesForUser(username));
	}
}
