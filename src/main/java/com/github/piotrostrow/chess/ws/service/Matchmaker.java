package com.github.piotrostrow.chess.ws.service;

import java.security.Principal;

public interface Matchmaker {

	void addToQueue(Principal user);

	void removeFromQueue(Principal user);
}
