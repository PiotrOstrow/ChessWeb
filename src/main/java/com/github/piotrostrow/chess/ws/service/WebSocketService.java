package com.github.piotrostrow.chess.ws.service;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.GameResult;
import com.github.piotrostrow.chess.ws.dto.GameOver;
import com.github.piotrostrow.chess.ws.dto.Move;
import com.github.piotrostrow.chess.ws.dto.Start;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

	private final SimpMessagingTemplate simpMessagingTemplate;

	public WebSocketService(SimpMessagingTemplate simpMessagingTemplate) {
		this.simpMessagingTemplate = simpMessagingTemplate;
	}

	public void sendStartGame(String whiteUsername, String blackUsername) {
		simpMessagingTemplate.convertAndSendToUser(whiteUsername, "/topic/game-start", new Start(Color.WHITE, blackUsername));
		simpMessagingTemplate.convertAndSendToUser(blackUsername, "/topic/game-start", new Start(Color.BLACK, whiteUsername));
	}

	public void sendMove(String username, Move move) {
		simpMessagingTemplate.convertAndSendToUser(username, "/topic/game-move", move);
	}

	public void sendGameOver(String username, GameResult gameResult) {
		simpMessagingTemplate.convertAndSendToUser(username, "/topic/game-over", new GameOver(gameResult));
	}
}
