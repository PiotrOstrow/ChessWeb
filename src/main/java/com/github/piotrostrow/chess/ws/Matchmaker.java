package com.github.piotrostrow.chess.ws;

import com.github.piotrostrow.chess.domain.User;

public interface Matchmaker {

	void addToQueue(User user);

	void removeFromQueue(User user);
}
