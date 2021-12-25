package com.github.piotrostrow.chess.rest.serivce;

import com.github.piotrostrow.chess.entity.GameEntity;
import com.github.piotrostrow.chess.entity.GamePlayedEntity;
import com.github.piotrostrow.chess.entity.UserEntity;
import com.github.piotrostrow.chess.repository.GamePlayedRepository;
import com.github.piotrostrow.chess.repository.GameRepository;
import com.github.piotrostrow.chess.repository.UserRepository;
import com.github.piotrostrow.chess.ws.GameSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.Principal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ModelMapper.class)
class GameServiceTest {

	private final Principal white = new UsernamePasswordAuthenticationToken("user_white", "");
	private final Principal black = new UsernamePasswordAuthenticationToken("user_black", "");

	@Autowired
	private ModelMapper modelMapper;

	@Test
	void testSaveGame() {
		GamePlayedRepository gamePlayedRepository = mock(GamePlayedRepository.class);
		GameRepository gameRepository = mock(GameRepository.class);
		UserRepository userRepository = mock(UserRepository.class);

		when(userRepository.findByUsername(any())).thenAnswer(e -> Optional.of(new UserEntity(e.getArgument(0))));

		GameService gameService = new GameService(gamePlayedRepository, gameRepository, userRepository, modelMapper);

		gameService.saveGame(new GameSession(white, black));

		ArgumentCaptor<GameEntity> argumentCaptor = ArgumentCaptor.forClass(GameEntity.class);
		verify(gameRepository, times(1)).save(argumentCaptor.capture());

		ArgumentCaptor<GamePlayedEntity> gamePlayedArgumentCaptor = ArgumentCaptor.forClass(GamePlayedEntity.class);
		verify(gamePlayedRepository, times(2)).save(gamePlayedArgumentCaptor.capture());

		assertThat(argumentCaptor.getValue().getGamesPlayed()).hasSize(2).containsAll(gamePlayedArgumentCaptor.getAllValues());
	}
}