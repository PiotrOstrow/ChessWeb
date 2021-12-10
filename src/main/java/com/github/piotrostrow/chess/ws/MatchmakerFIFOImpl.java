package com.github.piotrostrow.chess.ws;

import com.github.piotrostrow.chess.domain.User;
import org.springframework.stereotype.Component;

@Component
public class MatchmakerFIFOImpl implements Matchmaker {

	private final GameManager gameManager;

	private User userInQueue;

	public MatchmakerFIFOImpl(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@Override
	public synchronized void addToQueue(User user) {
		if(gameManager.isPlaying(user)){
			return;
		}

		if(this.userInQueue == null) {
			this.userInQueue = user;
		} else if(!this.userInQueue.getName().equals(user.getName())){
			gameManager.startGame(this.userInQueue, user);
			this.userInQueue = null;
		}
	}

	@Override
	public synchronized void removeFromQueue(User user) {
		if(this.userInQueue.getName().equals(user.getName())) {
			this.userInQueue = null;
		}
	}
}
