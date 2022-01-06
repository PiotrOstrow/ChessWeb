package com.github.piotrostrow.chess.rest.serivce;

import com.github.piotrostrow.chess.domain.chess.PuzzleRatingCalculator;
import com.github.piotrostrow.chess.entity.PuzzleEntity;
import com.github.piotrostrow.chess.entity.PuzzleThemeEntity;
import com.github.piotrostrow.chess.entity.UserEntity;
import com.github.piotrostrow.chess.repository.PuzzleRepository;
import com.github.piotrostrow.chess.repository.PuzzleThemeRepository;
import com.github.piotrostrow.chess.repository.UserRepository;
import com.github.piotrostrow.chess.rest.dto.PuzzleDto;
import com.github.piotrostrow.chess.rest.dto.PuzzleSolutionDto;
import com.github.piotrostrow.chess.rest.dto.PuzzleSolutionResponse;
import com.github.piotrostrow.chess.rest.exception.ApiException;
import com.github.piotrostrow.chess.rest.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

// TODO: testSubmitSolutionAlternativeLastMoveEndsInAMate
// TODO: testSubmitSolutionPromoteMove
class PuzzleServiceTest {

	private PuzzleService puzzleService;

	private final PuzzleRepository puzzleRepository = mock(PuzzleRepository.class);
	private final PuzzleThemeRepository puzzleThemeRepository = mock(PuzzleThemeRepository.class);
	private final UserRepository userRepository = mock(UserRepository.class);

	private final ModelMapper modelMapper = new ModelMapper();
	private final PuzzleRatingCalculator puzzleRatingCalculator = new PuzzleRatingCalculator();

	private final List<PuzzleEntity> puzzleEntities = IntStream.range(6, 20)
			.mapToObj(e -> new PuzzleEntity(e, "fen" + e, "e2e3", e * 100, Collections.emptySet()))
			.collect(Collectors.toList());

	private final Principal principal = new UsernamePasswordAuthenticationToken("username231", "");
	private final UserEntity userEntity = new UserEntity(principal.getName());

	@BeforeEach
	void setUp() {
		userEntity.setPuzzleRating(1200);

		when(userRepository.findByUsername(principal.getName())).thenReturn(Optional.of(userEntity));

		when(puzzleRepository.getMinRating()).thenReturn(puzzleEntities.stream().map(PuzzleEntity::getRating).min(Comparator.naturalOrder()));
		when(puzzleRepository.getMaxRating()).thenReturn(puzzleEntities.stream().map(PuzzleEntity::getRating).max(Comparator.naturalOrder()));

		puzzleService = new PuzzleService(puzzleRepository, puzzleThemeRepository, userRepository, modelMapper, puzzleRatingCalculator);
	}

	@Test
	void testCreatePuzzleThemeDoesNotExistShouldCreateNewTheme() {
		String theme = "Theme1";
		PuzzleDto puzzleDto = new PuzzleDto("fen", List.of("e2e3"), 1000, List.of(theme));

		when(puzzleThemeRepository.findByName(theme)).thenReturn(Optional.empty());

		puzzleService.createPuzzle(puzzleDto);

		verify(puzzleThemeRepository, times(1)).save(any());
		verify(puzzleRepository, times(1)).save(any());
	}

	@Test
	void testCreatePuzzleThemeExistsShouldNotCreateNewTheme() {
		String theme = "Theme1";
		PuzzleDto puzzleDto = new PuzzleDto("fen", List.of("e2e3"), 1000, List.of(theme));

		when(puzzleThemeRepository.findByName(theme)).thenReturn(Optional.of(new PuzzleThemeEntity(theme)));

		puzzleService.createPuzzle(puzzleDto);

		verify(puzzleThemeRepository, never()).save(any());
		verify(puzzleRepository, times(1)).save(any());
	}

	@Test
	void testCreatePuzzleSomeThemesExistsShouldCreateNonExistingThemes() {
		String existingTheme = "Theme1";
		String nonExistingTheme = "Theme2";
		PuzzleDto puzzleDto = new PuzzleDto("fen", List.of("e2e3"), 1000, List.of(existingTheme, nonExistingTheme));

		when(puzzleThemeRepository.findByName(nonExistingTheme)).thenReturn(Optional.empty());
		when(puzzleThemeRepository.findByName(existingTheme)).thenReturn(Optional.of(new PuzzleThemeEntity(existingTheme)));

		puzzleService.createPuzzle(puzzleDto);

		ArgumentCaptor<PuzzleThemeEntity> savedCaptor = ArgumentCaptor.forClass(PuzzleThemeEntity.class);

		verify(puzzleThemeRepository, times(1)).save(savedCaptor.capture());
		verify(puzzleThemeRepository, times(2)).findByName(any());
		verify(puzzleRepository, times(1)).save(any());

		assertThat(savedCaptor.getValue()).extracting(PuzzleThemeEntity::getName).isEqualTo(nonExistingTheme);
	}

	@Test
	void testCreatePuzzlesSharedThemeDoesNotExistShouldOnlyCreateOnce() {
		String theme = "Theme1";
		List<PuzzleDto> puzzles = List.of(
				new PuzzleDto("fen1", List.of("e2e3"), 1000, List.of(theme)),
				new PuzzleDto("fen1", List.of("e2e3"), 1000, List.of(theme))
		);

		when(puzzleThemeRepository.findByName(theme))
				.thenReturn(Optional.empty())
				.thenReturn(Optional.of(new PuzzleThemeEntity(theme)));

		puzzleService.createPuzzles(puzzles);

		verify(puzzleThemeRepository, times(1)).save(any());
	}

	@Test
	void testGetPuzzleByIdPuzzleDoesNotExistShouldThrowNotFoundException() {
		when(puzzleRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> puzzleService.getPuzzleById(1L)).isInstanceOf(NotFoundException.class);
	}

	@Test
	void testGetPuzzleByIdReturnsMappedDto() {
		PuzzleEntity puzzleEntity = puzzleEntities.get(0);
		when(puzzleRepository.findById(1L)).thenReturn(Optional.of(puzzleEntity));

		PuzzleDto actual = puzzleService.getPuzzleById(1L);

		assertThat(actual).isEqualTo(modelMapper.map(puzzleEntity, PuzzleDto.class));
	}

	@Test
	void testGetAllPuzzles() {
		when(puzzleRepository.findAll()).thenReturn(puzzleEntities);

		List<PuzzleDto> allPuzzles = puzzleService.getAllPuzzles();

		List<PuzzleDto> expected = puzzleEntities.stream().map(e -> modelMapper.map(e, PuzzleDto.class)).collect(Collectors.toList());

		assertThat(allPuzzles).isEqualTo(expected);
	}

	@Test
	void testDeletePuzzle() {
		PuzzleEntity puzzleEntity = puzzleEntities.get(0);
		when(puzzleRepository.findById(1L)).thenReturn(Optional.of(puzzleEntity));

		puzzleService.deletePuzzle(1L);

		verify(puzzleRepository, times(1)).delete(puzzleEntity);
	}

	@Test
	void testDeletePuzzleDoesNotExistShouldThrowNotFoundException() {
		when(puzzleRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> puzzleService.deletePuzzle(1L)).isInstanceOf(NotFoundException.class);
	}

	@Test
	void testGetRandomPuzzleNoPuzzleExistThrowsNotFoundException() {
		when(puzzleRepository.countAllByRatingBetween(anyInt(), anyInt())).thenReturn(0L);
		when(puzzleRepository.getMinRating()).thenReturn(Optional.empty());
		when(puzzleRepository.getMaxRating()).thenReturn(Optional.empty());

		assertThatThrownBy(() -> puzzleService.getRandomPuzzle(principal)).isInstanceOf(NotFoundException.class);
	}

	@Test
	void testGetRandomPuzzleLooksUpWithinUsersRating() {
		List<PuzzleEntity> puzzleEntities = List.of(new PuzzleEntity());
		Page<PuzzleEntity> page = new PageImpl<>(puzzleEntities, PageRequest.of(1, 1), puzzleEntities.size());

		int min = userEntity.getPuzzleRating() - PuzzleService.RANDOM_PUZZLE_RATING_RANGE;
		int max = userEntity.getPuzzleRating() + PuzzleService.RANDOM_PUZZLE_RATING_RANGE;

		when(puzzleRepository.countAllByRatingBetween(min, max)).thenReturn((long) puzzleEntities.size());
		when(puzzleRepository.findAllPagedRatingBetween(any(), eq(min), eq(max))).thenReturn(page);

		puzzleService.getRandomPuzzle(principal);

		verify(puzzleRepository, times(1)).findAllPagedRatingBetween(any(), eq(min), eq(max));
	}

	@Test
	void testGetRandomPuzzleDoesNotGoBellowLowestRatedPuzzle() {
		userEntity.setPuzzleRating(400);

		Page<PuzzleEntity> page = new PageImpl<>(puzzleEntities, PageRequest.of(1, 1), puzzleEntities.size());

		int min = puzzleEntities.stream().map(PuzzleEntity::getRating).min(Comparator.naturalOrder()).orElseThrow();
		int max = min + PuzzleService.RANDOM_PUZZLE_RATING_RANGE * 2;

		when(puzzleRepository.countAllByRatingBetween(min, max)).thenReturn((long) puzzleEntities.size());
		when(puzzleRepository.findAllPagedRatingBetween(any(), eq(min), eq(max))).thenReturn(page);

		puzzleService.getRandomPuzzle(principal);

		verify(puzzleRepository, times(1)).findAllPagedRatingBetween(any(), eq(min), eq(max));
	}

	@Test
	void testGetRandomPuzzleDoesNotGoAboveHighestRatedPuzzle() {
		userEntity.setPuzzleRating(2800);

		Page<PuzzleEntity> page = new PageImpl<>(puzzleEntities, PageRequest.of(1, 1), puzzleEntities.size());

		int max = puzzleEntities.stream().map(PuzzleEntity::getRating).max(Comparator.naturalOrder()).orElseThrow();
		int min = max - PuzzleService.RANDOM_PUZZLE_RATING_RANGE * 2;

		when(puzzleRepository.countAllByRatingBetween(min, max)).thenReturn((long) puzzleEntities.size());
		when(puzzleRepository.findAllPagedRatingBetween(any(), eq(min), eq(max))).thenReturn(page);

		puzzleService.getRandomPuzzle(principal);

		verify(puzzleRepository, times(1)).findAllPagedRatingBetween(any(), eq(min), eq(max));
	}

	@Test
	void testSubmitSolutionPuzzleDoesNotExistShouldThrowApiException() {
		when(puzzleRepository.findById(anyLong())).thenReturn(Optional.empty());

		PuzzleSolutionDto solution = new PuzzleSolutionDto();
		Principal principal = new UsernamePasswordAuthenticationToken("", "");

		assertThatThrownBy(() -> puzzleService.submitSolution(solution, principal)).isInstanceOf(ApiException.class)
				.extracting("status").isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void testSubmitSolutionIncorrectSolution() {
		PuzzleEntity puzzleEntity = new PuzzleEntity();
		puzzleEntity.setMoves("e2e3 e7e6");
		puzzleEntity.setRating(1250);

		PuzzleSolutionDto solution = new PuzzleSolutionDto();
		solution.setId(puzzleEntity.getId());
		solution.setMoves(List.of(puzzleEntity.getMoves().split(" ")));

		int initialPlayerRating = userEntity.getPuzzleRating();

		when(puzzleRepository.findById(puzzleEntity.getId())).thenReturn(Optional.of(puzzleEntity));

		PuzzleSolutionResponse puzzleSolutionResponse = puzzleService.submitSolution(solution, principal);

		assertThat(puzzleSolutionResponse.isCorrect()).isTrue();
		assertThat(puzzleSolutionResponse.getDelta()).isPositive();
		assertThat(puzzleSolutionResponse.getRating()).isEqualTo(initialPlayerRating + puzzleSolutionResponse.getDelta());
		assertThat(userEntity.getPuzzleRating()).isEqualTo(initialPlayerRating + puzzleSolutionResponse.getDelta());

		verify(userRepository, times(1)).save(userEntity);
	}

	@Test
	void testSubmitSolutionCorrectSolution() {
		PuzzleEntity puzzleEntity = new PuzzleEntity();
		puzzleEntity.setMoves("e2e3 e7e6");
		puzzleEntity.setRating(1250);

		PuzzleSolutionDto solution = new PuzzleSolutionDto();
		solution.setId(puzzleEntity.getId());
		solution.setMoves(List.of("e2e3", "e7e5"));

		int initialPlayerRating = userEntity.getPuzzleRating();

		when(puzzleRepository.findById(puzzleEntity.getId())).thenReturn(Optional.of(puzzleEntity));

		PuzzleSolutionResponse puzzleSolutionResponse = puzzleService.submitSolution(solution, principal);

		assertThat(puzzleSolutionResponse.isCorrect()).isFalse();
		assertThat(puzzleSolutionResponse.getDelta()).isNegative();
		assertThat(puzzleSolutionResponse.getRating()).isEqualTo(initialPlayerRating + puzzleSolutionResponse.getDelta());
		assertThat(userEntity.getPuzzleRating()).isEqualTo(initialPlayerRating + puzzleSolutionResponse.getDelta());

		verify(userRepository, times(1)).save(userEntity);
	}
}