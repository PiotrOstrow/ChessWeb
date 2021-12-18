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

	@Test
	void testRankStartingWithGap() {
		Fen fen = new Fen("1k6/8/8/8/8/8/8/1K6 w KQkq - 0 1");
		Map<String, Piece> actual = fen.getPieces().stream()
				.collect(Collectors.toMap(e -> e.getPosition().getNotation(), e -> e));

		assertThat(actual).hasSize(2);
		assertThat(actual.get("b8")).isExactlyInstanceOf(King.class).extracting(Piece::getColor).isEqualTo(Color.BLACK);
		assertThat(actual.get("b1")).isExactlyInstanceOf(King.class).extracting(Piece::getColor).isEqualTo(Color.WHITE);
	}

	@Test
	void testGetActiveColorWhite() {
		Fen fen = new Fen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		assertThat(fen.getActiveColor()).isEqualTo(Color.WHITE);
	}

	@Test
	void testGetActiveColorBlack() {
		Fen fen = new Fen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1");
		assertThat(fen.getActiveColor()).isEqualTo(Color.BLACK);
	}

	@Test
	void testCastlingAvailabilityAllAvailable() {
		Fen fen = new Fen("r1bqkb1r/pppp1ppp/2n1pn2/8/2B5/4PN2/PPPP1PPP/RNBQK2R w KQkq - 0 1");

		assertThat(fen.getCastlingAvailability()).hasSize(CastlingMove.values().length);
	}

	@Test
	void testEnPassantTargetSquare() {
		Fen fen = new Fen("r1bqkbnr/ppppp1pp/2n5/4Pp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f5 0 1");

		assertThat(fen.getEnPassantTarget()).isPresent().contains(new Position("f5"));
	}

	@Test
	void testEnPassantTargetSquareNone() {
		Fen fen = new Fen("r1bqkbnr/ppppp1pp/2n5/4Pp2/8/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");

		assertThat(fen.getEnPassantTarget()).isEmpty();
	}
}