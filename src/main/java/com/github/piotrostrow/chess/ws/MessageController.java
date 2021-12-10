package com.github.piotrostrow.chess.ws;

import com.github.piotrostrow.chess.domain.User;
import com.github.piotrostrow.chess.ws.dto.Move;
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
		matchmaker.addToQueue(new User(principal.getName()));
	}

	@MessageMapping("/move")
	public void handleMove(Move move, Principal principal) {
		gameManager.move(principal, move);
	}
}
