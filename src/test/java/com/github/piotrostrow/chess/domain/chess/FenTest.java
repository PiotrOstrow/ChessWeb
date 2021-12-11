package com.github.piotrostrow.chess.domain.chess;

import com.github.piotrostrow.chess.domain.chess.pieces.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class FenTest {

	@Test
	void testDefaultPosition() {
		Fen fen = Fen.DEFAULT_STARTING_POSITION;

		Map<String, Piece> actual = fen.getPieces().stream()
				.collect(Collectors.toMap(e -> e.getPosition().getNotation(), e -> e));

		List<Position> blackPieces = fen.getPieces().stream()
				.filter(e -> e.getColor() == Color.BLACK)
				.map(Piece::getPosition)
				.collect(Collectors.toList());

		assertThat(blackPieces).hasSize(16);
		blackPieces.forEach(e -> assertThat(e.getY()).isGreaterThanOrEqualTo(6));

		List<Position> whitePieces = fen.getPieces().stream()
				.filter(e -> e.getColor() == Color.WHITE)
				.map(Piece::getPosition)
				.collect(Collectors.toList());

		assertThat(whitePieces).hasSize(16);
		whitePieces.forEach(e -> assertThat(e.getY()).isLessThanOrEqualTo(2));

		assertThat(actual.get("a8")).isExactlyInstanceOf(Rook.class);
		assertThat(actual.get("b8")).isExactlyInstanceOf(Knight.class);
		assertThat(actual.get("c8")).isExactlyInstanceOf(Bishop.class);
		assertThat(actual.get("d8")).isExactlyInstanceOf(Queen.class);
		assertThat(actual.get("e8")).isExactlyInstanceOf(King.class);
		assertThat(actual.get("f8")).isExactlyInstanceOf(Bishop.class);
		assertThat(actual.get("g8")).isExactlyInstanceOf(Knight.class);
		assertThat(actual.get("h8")).isExactlyInstanceOf(Rook.class);

		assertThat(actual.get("a1")).isExactlyInstanceOf(Rook.class);
		assertThat(actual.get("b1")).isExactlyInstanceOf(Knight.class);
		assertThat(actual.get("c1")).isExactlyInstanceOf(Bishop.class);
		assertThat(actual.get("d1")).isExactlyInstanceOf(Queen.class);
		assertThat(actual.get("e1")).isExactlyInstanceOf(King.class);
		assertThat(actual.get("f1")).isExactlyInstanceOf(Bishop.class);
		assertThat(actual.get("g1")).isExactlyInstanceOf(Knight.class);
		assertThat(actual.get("h1")).isExactlyInstanceOf(Rook.class);

		for (int i = 0; i < 8; i++) {
			assertThat(actual.get(String.valueOf((char) (((byte) 'a') + i)) + '7').getColor()).isEqualTo(Color.BLACK);
			assertThat(actual.get(String.valueOf((char) (((byte) 'a') + i)) + '2').getColor()).isEqualTo(Color.WHITE);
		}
	}
}