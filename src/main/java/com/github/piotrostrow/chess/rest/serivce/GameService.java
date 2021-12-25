package com.github.piotrostrow.chess.rest.serivce;

import com.github.piotrostrow.chess.domain.chess.Color;
import com.github.piotrostrow.chess.domain.chess.PgnSerializer;
import com.github.piotrostrow.chess.entity.GameEntity;
import com.github.piotrostrow.chess.entity.GamePlayedEntity;
import com.github.piotrostrow.chess.entity.UserEntity;
import com.github.piotrostrow.chess.repository.GamePlayedRepository;
import com.github.piotrostrow.chess.repository.GameRepository;
import com.github.piotrostrow.chess.repository.UserRepository;
import com.github.piotrostrow.chess.rest.dto.GameDto;
import com.github.piotrostrow.chess.util.Util;
import com.github.piotrostrow.chess.ws.GameSession;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GameService {

	private final GamePlayedRepository gamePlayedRepository;
	private final GameRepository gameRepository;
	private final UserRepository userRepository;

	private final ModelMapper modelMapper;

	public GameService(GamePlayedRepository gamePlayedRepository, GameRepository gameRepository, UserRepository userRepository, ModelMapper modelMapper) {
		this.gamePlayedRepository = gamePlayedRepository;
		this.gameRepository = gameRepository;
		this.userRepository = userRepository;
		this.modelMapper = modelMapper;
	}

	public List<GameDto> getGames() {
		return Util.stream(gameRepository.findAll())
				.map(e -> modelMapper.map(e, GameDto.class))
				.collect(Collectors.toList());
	}

	public List<GameDto> getGamesForUser(String username) {
		return List.of();
	}

	public void saveGame(GameSession gameSession) {
		UserEntity white = userRepository.findByUsername(gameSession.getWhite().getName()).orElseThrow();
		UserEntity black = userRepository.findByUsername(gameSession.getBlack().getName()).orElseThrow();

		String pgnSerialized = PgnSerializer.serialize(gameSession.getGame());

		GameEntity gameEntity = new GameEntity();
		gameEntity.setPgn(pgnSerialized);
		gameEntity.setWinner(Color.WHITE);
		gameRepository.save(gameEntity);

		GamePlayedEntity whiteGamePlayedEntity = new GamePlayedEntity();
		whiteGamePlayedEntity.setColor(Color.WHITE);
		whiteGamePlayedEntity.setGame(gameEntity);
		white.addGamePlayed(whiteGamePlayedEntity);
		gamePlayedRepository.save(whiteGamePlayedEntity);

		GamePlayedEntity blackGamePlayedEntity = new GamePlayedEntity();
		blackGamePlayedEntity.setColor(Color.BLACK);
		blackGamePlayedEntity.setGame(gameEntity);
		black.addGamePlayed(blackGamePlayedEntity);
		gamePlayedRepository.save(blackGamePlayedEntity);
	}

}
