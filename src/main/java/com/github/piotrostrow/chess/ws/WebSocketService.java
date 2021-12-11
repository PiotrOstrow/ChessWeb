package com.github.piotrostrow.chess.ws;

import com.github.piotrostrow.chess.ws.dto.Message;
import com.github.piotrostrow.chess.ws.dto.Move;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

	private final SimpMessagingTemplate simpMessagingTemplate;

	public WebSocketService(SimpMessagingTemplate simpMessagingTemplate) {
		this.simpMessagingTemplate = simpMessagingTemplate;
	}

	public void sendStartGame(String white, String black) {
		simpMessagingTemplate.convertAndSendToUser(white, "/topic/game-start", new Message("start"));
		simpMessagingTemplate.convertAndSendToUser(black, "/topic/game-start", new Message("start"));
	}

	public void sendMove(String username, Move move) {
		simpMessagingTemplate.convertAndSendToUser(username, "/topic/game-move", move);
	}
}
