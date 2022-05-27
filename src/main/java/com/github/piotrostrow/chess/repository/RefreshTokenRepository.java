package com.github.piotrostrow.chess.repository;

import com.github.piotrostrow.chess.entity.RefreshTokenEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshTokenEntity, UUID> {
}
