package com.github.piotrostrow.chess.repository;

import com.github.piotrostrow.chess.entity.GamePlayedEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamePlayedRepository extends CrudRepository<GamePlayedEntity, Long> {

}
