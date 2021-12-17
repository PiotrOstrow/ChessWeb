package com.github.piotrostrow.chess.rest.serivce;

import com.github.piotrostrow.chess.entity.RoleEntity;
import com.github.piotrostrow.chess.repository.RoleRepository;
import com.github.piotrostrow.chess.security.Role;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RoleServiceImplTest {

	@Test
	void testPostConstructCreatesAllRoles() {
		RoleRepository roleRepository = mock(RoleRepository.class);
		when(roleRepository.findAll()).thenReturn(Collections.emptySet());

		new RoleServiceImpl(roleRepository).createDbEntitiesIfNeeded();

		ArgumentCaptor<RoleEntity> argumentCaptor = ArgumentCaptor.forClass(RoleEntity.class);
		verify(roleRepository, times(Role.values().length)).save(argumentCaptor.capture());
		assertThat(argumentCaptor.getAllValues()).map(RoleEntity::getRole).containsExactly(Role.values());
	}

	@Test
	void testPostConstructCreatesNoRoles() {
		List<RoleEntity> createdRoles = Arrays.stream(Role.values())
				.map(RoleEntity::new)
				.collect(Collectors.toList());

		RoleRepository roleRepository = mock(RoleRepository.class);
		when(roleRepository.findAll()).thenReturn(createdRoles);

		new RoleServiceImpl(roleRepository).createDbEntitiesIfNeeded();

		verify(roleRepository, times(0)).save(any());
	}

	@Test
	void testPostConstructCreatesOneRoles() {
		Role createdRole = Role.USER;
		Set<RoleEntity> existingRoles = Arrays.stream(Role.values())
				.filter(e -> e != createdRole)
				.map(RoleEntity::new)
				.collect(Collectors.toSet());

		RoleRepository roleRepository = mock(RoleRepository.class);
		when(roleRepository.findAll()).thenReturn(existingRoles);

		new RoleServiceImpl(roleRepository).createDbEntitiesIfNeeded();

		ArgumentCaptor<RoleEntity> argumentCaptor = ArgumentCaptor.forClass(RoleEntity.class);
		verify(roleRepository, times(1)).save(argumentCaptor.capture());
		assertThat(argumentCaptor.getAllValues()).hasSize(1).map(RoleEntity::getRole).contains(createdRole);
	}
}