package com.github.piotrostrow.chess.rest.serivce;

import com.github.piotrostrow.chess.entity.PuzzleEntity;
import com.github.piotrostrow.chess.entity.PuzzleThemeEntity;
import com.github.piotrostrow.chess.repository.PuzzleRepository;
import com.github.piotrostrow.chess.repository.PuzzleThemeRepository;
import com.github.piotrostrow.chess.rest.dto.PuzzleDto;
import com.github.piotrostrow.chess.util.Util;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PuzzleService {

	private final PuzzleRepository puzzleRepository;
	private final PuzzleThemeRepository puzzleThemeRepository;
	private final ModelMapper modelMapper;

	private final Random random = new Random();

	public PuzzleService(PuzzleRepository puzzleRepository, PuzzleThemeRepository puzzleThemeRepository, ModelMapper modelMapper) {
		this.puzzleRepository = puzzleRepository;
		this.puzzleThemeRepository = puzzleThemeRepository;
		this.modelMapper = modelMapper;
	}

	public void createPuzzles(List<PuzzleDto> puzzles) {
		puzzles.forEach(this::createPuzzle);
	}

	public void createPuzzle(PuzzleDto puzzleDto) {
		PuzzleEntity puzzleEntity = new PuzzleEntity();
		puzzleEntity.setFen(puzzleDto.getFen());
		puzzleEntity.setRating(puzzleDto.getRating());
		puzzleEntity.setMoves(String.join(" ", puzzleDto.getMoves()));

		Set<PuzzleThemeEntity> themes = puzzleDto.getThemes().stream()
				.map(this::getOrCreateTheme)
				.collect(Collectors.toSet());
		puzzleEntity.setThemes(themes);

		puzzleRepository.save(puzzleEntity);
	}

	private PuzzleThemeEntity getOrCreateTheme(String name) {
		return puzzleThemeRepository.findByName(name).orElseGet(() -> createTheme(name));
	}

	private PuzzleThemeEntity createTheme(String name) {
		PuzzleThemeEntity puzzleTheme = new PuzzleThemeEntity(name);
		puzzleThemeRepository.save(puzzleTheme);
		return puzzleTheme;
	}

	public long getPuzzleCount() {
		return puzzleRepository.count();
	}

	public List<PuzzleDto> getAllPuzzles() {
		return Util.stream(puzzleRepository.findAll())
				.map(e -> modelMapper.map(e, PuzzleDto.class))
				.collect(Collectors.toList());
	}

	public PuzzleDto getRandomPuzzle() {
		long count = puzzleRepository.count();

		Page<PuzzleEntity> rating = puzzleRepository.findAllPaged(PageRequest.of(random.nextInt((int) count), 1));

		return modelMapper.map(rating.stream().findFirst().orElseThrow(), PuzzleDto.class);
	}
}
