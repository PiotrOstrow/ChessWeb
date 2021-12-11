package com.github.piotrostrow.chess.domain.chess;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PositionTest {

	@Test
	void testNotation() {
		assertThat(new Position(0, 0).getNotation()).isEqualTo("a1");
		assertThat(new Position(0, 7).getNotation()).isEqualTo("a8");
		assertThat(new Position(7, 0).getNotation()).isEqualTo("h1");
		assertThat(new Position(7, 7).getNotation()).isEqualTo("h8");
	}
}