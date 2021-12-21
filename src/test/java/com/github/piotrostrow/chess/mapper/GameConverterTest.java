package com.github.piotrostrow.chess.mapper;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.entity.GameEntity;
import com.github.piotrostrow.chess.entity.GamePlayedEntity;
import com.github.piotrostrow.chess.entity.UserEntity;
import com.github.piotrostrow.chess.rest.dto.GameDto;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GameConverterTest {

	private static final String PGN = "pgn placeholder";
	private static final String USERNAME_WHITE = "username_white";
	private static final String USERNAME_BLACK = "username_white";

	@Test
	void testMapping() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new GameConverter());

		GameEntity gameEntity = new GameEntity();
		gameEntity.setPgn(PGN);
		gameEntity.setWinner(Color.BLACK);
		gameEntity.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));

		GamePlayedEntity gamePlayedWhite = createGamePlayedEntity(gameEntity, Color.WHITE, USERNAME_WHITE);
		GamePlayedEntity gamePlayedBlack = createGamePlayedEntity(gameEntity, Color.BLACK, USERNAME_BLACK);

		gameEntity.setGamesPlayed(Set.of(gamePlayedWhite, gamePlayedBlack));

		GameDto actual = modelMapper.map(gameEntity, GameDto.class);

		assertThat(actual.getPgn()).isEqualTo(PGN);
		assertThat(actual.getWhite()).isEqualTo(USERNAME_WHITE);
		assertThat(actual.getBlack()).isEqualTo(USERNAME_BLACK);
		assertThat(actual.getTimestamp()).isEqualTo(gameEntity.getTimestamp().toLocalDateTime());
	}

	private GamePlayedEntity createGamePlayedEntity(GameEntity gameEntity, Color color, String username) {
		GamePlayedEntity gamePlayedWhite = new GamePlayedEntity();
		gamePlayedWhite.setGame(gameEntity);
		gamePlayedWhite.setColor(color);
		UserEntity user = new UserEntity();
		user.setUsername(username);
		gamePlayedWhite.setUser(user);
		return gamePlayedWhite;
	}
}