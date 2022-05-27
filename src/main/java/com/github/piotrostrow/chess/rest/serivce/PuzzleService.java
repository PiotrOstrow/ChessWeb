package com.github.piotrostrow.chess.rest.serivce;

import com.github.piotrostrow.chess.domain.chess.PuzzleRatingCalculator;
import com.github.piotrostrow.chess.domain.chess.PuzzleSolutionValidator;
import com.github.piotrostrow.chess.entity.PuzzleDetailsEntity;
import com.github.piotrostrow.chess.entity.PuzzleEntity;
import com.github.piotrostrow.chess.entity.PuzzleThemeEntity;
import com.github.piotrostrow.chess.entity.UserEntity;
import com.github.piotrostrow.chess.repository.PuzzleRepository;
import com.github.piotrostrow.chess.repository.PuzzleThemeRepository;
import com.github.piotrostrow.chess.repository.UserRepository;
import com.github.piotrostrow.chess.rest.dto.puzzle.PuzzleDto;
import com.github.piotrostrow.chess.rest.dto.puzzle.PuzzleSolutionDto;
import com.github.piotrostrow.chess.rest.dto.puzzle.PuzzleSolutionResponse;
import com.github.piotrostrow.chess.rest.exception.BadRequestException;
import com.github.piotrostrow.chess.rest.exception.NotFoundException;
import com.github.piotrostrow.chess.util.Util;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PuzzleService {

	static final int RANDOM_PUZZLE_RATING_RANGE = 200;

	private final PuzzleRepository puzzleRepository;
	private final PuzzleThemeRepository puzzleThemeRepository;
	private final UserRepository userRepository;

	private final ModelMapper modelMapper;

	private final Random random = new Random();

	public PuzzleService(PuzzleRepository puzzleRepository,
						 PuzzleThemeRepository puzzleThemeRepository,
						 UserRepository userRepository,
						 ModelMapper modelMapper) {
		this.puzzleRepository = puzzleRepository;
		this.puzzleThemeRepository = puzzleThemeRepository;
		this.userRepository = userRepository;
		this.modelMapper = modelMapper;
	}

	public PuzzleDto getPuzzleById(Long id) {
		PuzzleEntity puzzleEntity = puzzleRepository.findById(id).orElseThrow(() -> new NotFoundException("puzzle does not exist"));
		return modelMapper.map(puzzleEntity, PuzzleDto.class);
	}

	public void createPuzzles(List<PuzzleDto> puzzles) {
		puzzles.forEach(this::createPuzzle);
	}

	public PuzzleDto createPuzzle(PuzzleDto puzzleDto) {
		PuzzleDetailsEntity puzzleDetailsEntity = new PuzzleDetailsEntity(puzzleDto.getFen(), String.join(" ", puzzleDto.getMoves()));
		Set<PuzzleThemeEntity> themes = puzzleDto.getThemes().stream()
				.map(this::getOrCreateTheme)
				.collect(Collectors.toSet());

		PuzzleEntity puzzleEntity = new PuzzleEntity(puzzleDetailsEntity, themes, puzzleDto.getRating());
		puzzleDetailsEntity.setPuzzleEntity(puzzleEntity);

		puzzleRepository.save(puzzleEntity);

		return modelMapper.map(puzzleEntity, PuzzleDto.class);
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

	public void deletePuzzle(Long id) {
		PuzzleEntity puzzleEntity = puzzleRepository.findById(id).orElseThrow(() -> new NotFoundException("puzzle does not exist"));
		puzzleRepository.delete(puzzleEntity);
	}

	public PuzzleDto getRandomPuzzle(Principal principal) {
		int playerRating = userRepository.findByUsername(principal.getName()).map(UserEntity::getPuzzleRating).orElseThrow();

		PuzzleEntity puzzleEntity = getRandomPuzzle(playerRating);

		return modelMapper.map(puzzleEntity, PuzzleDto.class);
	}

	private PuzzleEntity getRandomPuzzle(int playerRating) {
		try {
			int minRating = playerRating - RANDOM_PUZZLE_RATING_RANGE;
			int maxRating = playerRating + RANDOM_PUZZLE_RATING_RANGE;

			int minPuzzleRating = puzzleRepository.getMinRating().orElseThrow();
			int maxPuzzleRating = puzzleRepository.getMaxRating().orElseThrow();

			if (minRating < minPuzzleRating) {
				minRating = minPuzzleRating;
				maxRating = minPuzzleRating + RANDOM_PUZZLE_RATING_RANGE * 2;
			} else if (maxRating > maxPuzzleRating) {
				maxRating = maxPuzzleRating;
				minRating = maxPuzzleRating - RANDOM_PUZZLE_RATING_RANGE * 2;
			}

			long countWithinRange = puzzleRepository.countAllByRatingBetween(minRating, maxRating);

			Pageable pageable = PageRequest.of(random.nextInt((int) countWithinRange), 1);
			Page<PuzzleEntity> page = puzzleRepository.findAllPagedRatingBetween(pageable, minRating, maxRating);

			return page.stream().findFirst().orElseThrow();
		} catch (NoSuchElementException e) {
			throw new NotFoundException("No puzzles found");
		}
	}

	public PuzzleSolutionResponse submitSolution(PuzzleSolutionDto puzzleSolutionDto, Principal principal) {
		validateInputMoves(puzzleSolutionDto.getMoves());

		PuzzleEntity puzzleEntity = puzzleRepository.findById(puzzleSolutionDto.getId())
				.orElseThrow(() -> new NotFoundException("Puzzle does not exist"));

		UserEntity userEntity = userRepository.findByUsername(principal.getName()).orElseThrow();

		boolean isCorrectSolution = PuzzleSolutionValidator.isCorrectSolution(puzzleEntity, puzzleSolutionDto.getMoves());
		int delta = PuzzleRatingCalculator.calculateDelta(userEntity.getPuzzleRating(), puzzleEntity.getRating(), isCorrectSolution);

		userEntity.setPuzzleRating(userEntity.getPuzzleRating() + delta);
		userRepository.save(userEntity);

		return new PuzzleSolutionResponse(isCorrectSolution, userEntity.getPuzzleRating(), delta);
	}

	private void validateInputMoves(List<String> moves) {
		if (moves.stream().anyMatch(e -> !e.matches("([a-h][1-8]){2}[qnbr]?"))) {
			throw new BadRequestException("Invalid input - moves must be in UCI format");
		}
	}
}
