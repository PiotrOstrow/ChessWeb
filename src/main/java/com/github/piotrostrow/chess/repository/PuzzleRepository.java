package com.github.piotrostrow.chess.repository;

import com.github.piotrostrow.chess.entity.PuzzleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PuzzleRepository extends CrudRepository<PuzzleEntity, Long> {

	@Query("select p from PuzzleEntity p where p.rating >= :min and p.rating <= :max")
	Page<PuzzleEntity> findAllPagedRatingBetween(Pageable pageable, int min, int max);

	long countAllByRatingBetween(int min, int max);

	@Query("select min(p.rating) from PuzzleEntity p")
	Optional<Integer> getMinRating();

	@Query("select max(p.rating) from PuzzleEntity p")
	Optional<Integer> getMaxRating();
}
