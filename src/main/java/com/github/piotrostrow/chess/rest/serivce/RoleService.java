package com.github.piotrostrow.chess.rest.serivce;

import com.github.piotrostrow.chess.entity.RoleEntity;
import com.github.piotrostrow.chess.security.Role;

public interface RoleService {

	Iterable<RoleEntity> getAllRoles();

	RoleEntity getRole(Role role);
}
