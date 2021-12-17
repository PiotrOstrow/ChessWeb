package com.github.piotrostrow.chess.repository;

import com.github.piotrostrow.chess.entity.RoleEntity;
import com.github.piotrostrow.chess.security.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<RoleEntity, Long> {

	Optional<RoleEntity> findByRole(Role role);
}
