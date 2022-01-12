package com.github.piotrostrow.chess.rest.serivce;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.GameResult;
import com.github.piotrostrow.chess.entity.GameEntity;
import com.github.piotrostrow.chess.entity.GamePlayedEntity;
import com.github.piotrostrow.chess.entity.UserEntity;
import com.github.piotrostrow.chess.repository.GamePlayedRepository;
import com.github.piotrostrow.chess.repository.GameRepository;
import com.github.piotrostrow.chess.repository.UserRepository;
import com.github.piotrostrow.chess.rest.dto.GameDto;
import com.github.piotrostrow.chess.rest.exception.NotFoundException;
import com.github.piotrostrow.chess.ws.game.GameSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ModelMapper.class)
class GameServiceTest {

	@Autowired
	private ModelMapper modelMapper;

	private final GamePlayedRepository gamePlayedRepository = mock(GamePlayedRepository.class);
	private final GameRepository gameRepository = mock(GameRepository.class);
	private final UserRepository userRepository = mock(UserRepository.class);

	private final Principal white = new UsernamePasswordAuthenticationToken("user_white", "");
	private final Principal black = new UsernamePasswordAuthenticationToken("user_black", "");

	private final GameEntity game1 = new GameEntity(1L, Set.of(
			new GamePlayedEntity(1L, new UserEntity(white.getName()), null, Color.WHITE),
			new GamePlayedEntity(2L, new UserEntity(black.getName()), null, Color.BLACK)
	), "pgn2", GameResult.CHECKMATE, Color.WHITE, Timestamp.valueOf(LocalDateTime.now()));

	private final GameEntity game2 = new GameEntity(2L, Set.of(
			new GamePlayedEntity(3L, new UserEntity(white.getName()), null, Color.WHITE),
			new GamePlayedEntity(4L, new UserEntity(black.getName()), null, Color.BLACK)
	), "pgn2", GameResult.CHECKMATE, Color.WHITE, Timestamp.valueOf(LocalDateTime.now()));

	private GameService gameService;

	@BeforeEach
	void setUp() {
		gameService = new GameService(gamePlayedRepository, gameRepository, userRepository, modelMapper);
	}

	@Test
	void testSaveGame() {
		when(userRepository.findByUsername(any())).thenAnswer(e -> Optional.of(new UserEntity(e.getArgument(0))));

		GameSession gameSession = new GameSession(white, black, mock(TaskScheduler.class), null);
		gameService.saveGame(gameSession);

		ArgumentCaptor<GameEntity> argumentCaptor = ArgumentCaptor.forClass(GameEntity.class);
		verify(gameRepository, times(1)).save(argumentCaptor.capture());

		ArgumentCaptor<GamePlayedEntity> gamePlayedArgumentCaptor = ArgumentCaptor.forClass(GamePlayedEntity.class);
		verify(gamePlayedRepository, times(2)).save(gamePlayedArgumentCaptor.capture());

		GameEntity gameEntity = argumentCaptor.getValue();
		assertThat(gameEntity.getGameResult()).isEqualTo(gameSession.getGameResult());
		assertThat(gameEntity.getWinner()).isEqualTo(gameSession.getWinner().orElse(null));
		assertThat(gameEntity.getGamesPlayed()).hasSize(2).containsAll(gamePlayedArgumentCaptor.getAllValues());
	}

	@Test
	void testGetAllGamesShouldFetchAndReturnAllGames() {
		List<GameEntity> games = List.of(game1, game2);
		when(gameRepository.findAll()).thenReturn(games);

		List<GameDto> expected = games.stream().map(e -> modelMapper.map(e, GameDto.class)).collect(Collectors.toList());
		List<GameDto> actual = gameService.getGames();

		verify(gameRepository, times(1)).findAll();
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void testAllGamesNoGamesExistShouldReturnEmptyList() {
		when(gameRepository.findAll()).thenReturn(Collections.emptyList());

		List<GameDto> actual = gameService.getGames();

		assertThat(actual).isEmpty();
	}

	@Test
	void testGetAllGamesForUserShouldReturnAllUsersGames() {
		List<GameEntity> games = List.of(this.game1, game2);
		when(gameRepository.findAllByGamesPlayed_User_Username(white.getName())).thenReturn(games);
		when(userRepository.findByUsername(white.getName())).thenReturn(Optional.of(new UserEntity(white.getName())));

		List<GameDto> expected = games.stream().map(e -> modelMapper.map(e, GameDto.class)).collect(Collectors.toList());
		List<GameDto> actual = gameService.getGamesForUser(white.getName());

		verify(gameRepository, times(1)).findAllByGamesPlayed_User_Username(white.getName());
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void testGetAllGamesForNonExistingUserShouldThrowNotFoundException() {
		String nonExistingUser = "user333";
		when(userRepository.findByUsername(nonExistingUser)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> gameService.getGamesForUser(nonExistingUser)).isInstanceOf(NotFoundException.class);
	}

	@Test
	void testGetAllGamesForUserHasNoGamesShouldReturnEmptyList() {
		when(gameRepository.findAll()).thenReturn(Collections.emptyList());

		List<GameDto> actual = gameService.getGames();

		verify(gameRepository, times(1)).findAll();
		assertThat(actual).isEmpty();
	}
}