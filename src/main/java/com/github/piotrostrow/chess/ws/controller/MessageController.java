package com.github.piotrostrow.chess.ws.controller;

import com.github.piotrostrow.chess.ws.dto.Move;
import com.github.piotrostrow.chess.ws.service.GameManager;
import com.github.piotrostrow.chess.ws.service.Matchmaker;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class MessageController {

	private final Matchmaker matchmaker;
	private final GameManager gameManager;

	public MessageController(Matchmaker matchmaker, GameManager gameManager) {
		this.matchmaker = matchmaker;
		this.gameManager = gameManager;
	}

	@MessageMapping("/play")
	public void handlePlay(Principal principal) {
		matchmaker.addToQueue(principal);
	}

	@MessageMapping("/move")
	public void handleMove(Move move, Principal principal) {
		gameManager.move(principal, move);
	}
}
