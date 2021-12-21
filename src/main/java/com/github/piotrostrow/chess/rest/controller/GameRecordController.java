package com.github.piotrostrow.chess.rest.controller;

import com.github.piotrostrow.chess.rest.serivce.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
	public ResponseEntity<Object> get() {
		return ResponseEntity.ok(gameRecordService.getGames());
	}
}
