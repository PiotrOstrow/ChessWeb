package com.github.piotrostrow.chess.repository;

import com.github.piotrostrow.chess.entity.PuzzleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PuzzleRepository extends CrudRepository<PuzzleEntity, Long> {

	@Query("select p from PuzzleEntity p")
	Page<PuzzleEntity> findAllPaged(Pageable pageable);
}
