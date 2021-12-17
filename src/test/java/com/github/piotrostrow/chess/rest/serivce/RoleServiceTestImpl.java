package com.github.piotrostrow.chess.rest.serivce;

import com.github.piotrostrow.chess.entity.RoleEntity;
import com.github.piotrostrow.chess.security.Role;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RoleServiceTestImpl implements RoleService {

	@Override
	public Iterable<RoleEntity> getAllRoles() {
		return Arrays.stream(Role.values())
				.map(RoleEntity::new)
				.collect(Collectors.toList());
	}

	@Override
	public RoleEntity getRole(Role role) {
		return new RoleEntity(role);
	}
}
