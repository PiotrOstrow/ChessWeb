package com.github.piotrostrow.chess.repository;

import com.github.piotrostrow.chess.entity.GameEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends CrudRepository<GameEntity, Long> {

	List<GameEntity> findAllByGamesPlayed_User_Username(String username);
}
