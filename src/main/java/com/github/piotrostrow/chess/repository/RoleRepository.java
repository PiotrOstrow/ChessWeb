package com.github.piotrostrow.chess.repository;

import com.github.piotrostrow.chess.entity.RoleEntity;
import com.github.piotrostrow.chess.security.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Long> {

	Optional<RoleEntity> findByRole(Role role);
}
