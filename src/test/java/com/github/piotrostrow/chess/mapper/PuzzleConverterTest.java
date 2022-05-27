package com.github.piotrostrow.chess.mapper;

import com.github.piotrostrow.chess.entity.PuzzleDetailsEntity;
import com.github.piotrostrow.chess.entity.PuzzleEntity;
import com.github.piotrostrow.chess.entity.PuzzleThemeEntity;
import com.github.piotrostrow.chess.rest.dto.puzzle.PuzzleDto;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class PuzzleConverterTest {

	@Test
	void testMapping() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new PuzzleConverter());

		List<String> moves = List.of("e2e4", "e7e6", "d2d3");
		Set<String> themes = Set.of("Theme1", "Theme3", "theme9");

		Set<PuzzleThemeEntity> themeEntities = themes.stream()
				.map(PuzzleThemeEntity::new)
				.collect(Collectors.toSet());

		PuzzleDetailsEntity puzzleDetailsEntity = new PuzzleDetailsEntity("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", String.join(" ", moves));
		PuzzleEntity puzzleEntity = new PuzzleEntity(123, puzzleDetailsEntity, themeEntities, 1000);

		PuzzleDto actual = modelMapper.map(puzzleEntity, PuzzleDto.class);

		assertThat(actual.getId()).isEqualTo(puzzleEntity.getId());
		assertThat(actual.getFen()).isEqualTo(puzzleEntity.getFen());
		assertThat(actual.getMoves()).isEqualTo(moves);
		assertThat(actual.getThemes()).containsAll(themes);
	}
}