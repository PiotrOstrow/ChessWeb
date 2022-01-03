package com.github.piotrostrow.chess.repository;

import com.github.piotrostrow.chess.entity.PuzzleThemeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PuzzleThemeRepository extends CrudRepository<PuzzleThemeEntity, Long> {

	Optional<PuzzleThemeEntity> findByName(String name);
}
