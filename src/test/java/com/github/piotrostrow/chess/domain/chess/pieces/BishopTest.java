package com.github.piotrostrow.chess.domain.chess.pieces;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.Position;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class BishopTest {

	@Test
	void testBishopInCorner() {
		Bishop bishop = new Bishop(Color.BLACK, new Position(0, 0));

		Collection<Position> actual = bishop.getPseudoLegalMoves(Collections.emptyMap()).stream()
				.filter(e -> e.getX() == e.getY())
				.collect(Collectors.toSet());

		assertThat(actual).hasSize(7);
	}
}