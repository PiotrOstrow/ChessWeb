package com.github.piotrostrow.chess.ws;

import com.github.piotrostrow.chess.domain.User;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
public class WebSocketEventListener {

	private final GameManager gameManager;
	private final Matchmaker matchmaker;

	public WebSocketEventListener(GameManager gameManager, Matchmaker matchmaker) {
		this.gameManager = gameManager;
		this.matchmaker = matchmaker;
	}

	@EventListener
	public void handleSessionDisconnected(SessionDisconnectEvent event) {
		SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
		Principal principal = accessor.getUser();
		if(principal != null) {
			User user = new User(principal.getName());
			matchmaker.removeFromQueue(user);
			gameManager.disconnected(user);
		}
	}
}
