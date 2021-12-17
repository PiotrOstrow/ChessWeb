package com.github.piotrostrow.chess.ws.service;

import com.github.piotrostrow.chess.domain.User;

public interface Matchmaker {

	void addToQueue(User user);

	void removeFromQueue(User user);
}
