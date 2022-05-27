package com.github.piotrostrow.chess.mapper;

import com.github.piotrostrow.chess.entity.PuzzleEntity;
import com.github.piotrostrow.chess.entity.PuzzleThemeEntity;
import com.github.piotrostrow.chess.rest.dto.puzzle.PuzzleDto;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PuzzleConverter implements Converter<PuzzleEntity, PuzzleDto> {

	@Override
	public PuzzleDto convert(MappingContext<PuzzleEntity, PuzzleDto> context) {
		PuzzleEntity s = context.getSource();
		PuzzleDto d = context.getDestination() == null ? new PuzzleDto() : context.getDestination();

		d.setId(s.getId());
		d.setFen(s.getFen());
		d.setRating(s.getRating());
		d.setMoves(Arrays.asList(s.getMoves().split(" ")));
		d.setThemes(s.getThemes().stream().map(PuzzleThemeEntity::getName).collect(Collectors.toList()));

		return d;
	}
}
