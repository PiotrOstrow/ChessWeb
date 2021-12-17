package com.github.piotrostrow.chess.rest.serivce;

import com.github.piotrostrow.chess.entity.RoleEntity;
import com.github.piotrostrow.chess.repository.RoleRepository;
import com.github.piotrostrow.chess.security.Role;
import com.github.piotrostrow.chess.util.Util;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

	private final RoleRepository roleRepository;

	public RoleServiceImpl(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	@PostConstruct
	public void createDbEntitiesIfNeeded() {
		Set<Role> existingRoles = Util.stream(getAllRoles())
				.map(RoleEntity::getRole)
				.collect(Collectors.toSet());

		Arrays.stream(Role.values())
				.filter(Predicate.not(existingRoles::contains))
				.map(RoleEntity::new)
				.forEach(roleRepository::save);
	}

	@Override
	public Iterable<RoleEntity> getAllRoles() {
		return roleRepository.findAll();
	}

	@Override
	public RoleEntity getRole(Role role) {
		return roleRepository.findByRole(role).orElseThrow(IllegalStateException::new);
	}
}
