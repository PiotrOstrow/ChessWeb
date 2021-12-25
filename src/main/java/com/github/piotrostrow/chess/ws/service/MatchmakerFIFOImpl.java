package com.github.piotrostrow.chess.ws.service;

import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class MatchmakerFIFOImpl implements Matchmaker {

	private final GameManager gameManager;

	private Principal userInQueue;

	public MatchmakerFIFOImpl(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@Override
	public synchronized void addToQueue(Principal user) {
		if (gameManager.isPlaying(user)) {
			return;
		}

		if (this.userInQueue == null) {
			this.userInQueue = user;
		} else if (!this.userInQueue.getName().equals(user.getName())) {
			Principal white = Math.random() > 0.5 ? this.userInQueue : user;
			Principal black = white == this.userInQueue ? user : this.userInQueue;
			gameManager.startGame(white, black);
			this.userInQueue = null;
		}
	}

	@Override
	public synchronized void removeFromQueue(Principal user) {
		if (this.userInQueue != null && this.userInQueue.getName().equals(user.getName())) {
			this.userInQueue = null;
		}
	}
}
