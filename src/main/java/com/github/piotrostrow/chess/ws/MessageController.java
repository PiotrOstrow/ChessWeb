package com.github.piotrostrow.chess.ws;

import com.github.piotrostrow.chess.dto.Message;
import com.github.piotrostrow.chess.dto.Move;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class MessageController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

	private final SimpMessagingTemplate simpMessagingTemplate;

	public MessageController(SimpMessagingTemplate simpMessagingTemplate) {
		this.simpMessagingTemplate = simpMessagingTemplate;
	}

	@MessageMapping("/play")
	public void handlePlay(Principal principal) {
		LOGGER.info("{} wants to play", principal.getName());
	}

	@MessageMapping("/move")
	public void handleMove(Move move, Principal principal) {
		LOGGER.info("{} moved from {} to {}", principal.getName(), move.getFrom(), move.getTo());
		simpMessagingTemplate.convertAndSendToUser(principal.getName(),"/topic/private-messages", new Message("pong"));

	}
}
