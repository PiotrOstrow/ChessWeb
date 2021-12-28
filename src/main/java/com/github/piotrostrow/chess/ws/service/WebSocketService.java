package com.github.piotrostrow.chess.ws.service;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.GameResult;
import com.github.piotrostrow.chess.ws.dto.GameOver;
import com.github.piotrostrow.chess.ws.dto.GameStart;
import com.github.piotrostrow.chess.ws.dto.MoveResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

	private final SimpMessagingTemplate simpMessagingTemplate;

	public WebSocketService(SimpMessagingTemplate simpMessagingTemplate) {
		this.simpMessagingTemplate = simpMessagingTemplate;
	}

	public void sendStartGame(String whiteUsername, String blackUsername, int time) {
		simpMessagingTemplate.convertAndSendToUser(whiteUsername, "/topic/game-start", new GameStart(Color.WHITE, blackUsername, time));
		simpMessagingTemplate.convertAndSendToUser(blackUsername, "/topic/game-start", new GameStart(Color.BLACK, whiteUsername, time));
	}

	public void sendMove(String username, MoveResponse moveResponse) {
		simpMessagingTemplate.convertAndSendToUser(username, "/topic/game-move", moveResponse);
	}

	public void sendGameOver(String username, GameResult gameResult) {
		simpMessagingTemplate.convertAndSendToUser(username, "/topic/game-over", new GameOver(gameResult));
	}
}
