package com.github.piotrostrow.chess.mapper;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.entity.GameEntity;
import com.github.piotrostrow.chess.entity.GamePlayedEntity;
import com.github.piotrostrow.chess.rest.dto.GameDto;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.Map;
import java.util.stream.Collectors;

public class GameConverter implements Converter<GameEntity, GameDto> {

	@Override
	public GameDto convert(MappingContext<GameEntity, GameDto> context) {
		GameEntity s = context.getSource();
		GameDto d = context.getDestination() == null ? new GameDto() : context.getDestination();

		Map<Color, String> usernameByColor = s.getGamesPlayed().stream()
				.collect(Collectors.toMap(GamePlayedEntity::getColor, e -> e.getUser().getUsername()));

		d.setPgn(s.getPgn());
		d.setWhite(usernameByColor.get(Color.WHITE));
		d.setBlack(usernameByColor.get(Color.BLACK));
		d.setTimestamp(s.getTimestamp().toLocalDateTime());

		return d;
	}
}
