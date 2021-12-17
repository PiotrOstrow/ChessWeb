package com.github.piotrostrow.chess.rest.serivce;

import com.github.piotrostrow.chess.entity.RoleEntity;
import com.github.piotrostrow.chess.entity.UserEntity;
import com.github.piotrostrow.chess.repository.UserRepository;
import com.github.piotrostrow.chess.rest.dto.UserDto;
import com.github.piotrostrow.chess.rest.exception.BadRequestException;
import com.github.piotrostrow.chess.rest.exception.ConflictException;
import com.github.piotrostrow.chess.security.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ModelMapper.class, BCryptPasswordEncoder.class, RoleServiceTestImpl.class})
class UserServiceTest {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private RoleService roleService;

	@Test
	void testCreateUserNoPasswordThrowsException() {
		UserRepository userRepository = mock(UserRepository.class);

		UserService userService = new UserService(userRepository, passwordEncoder, modelMapper, roleService);

		UserDto userDto = new UserDto();
		assertThatThrownBy(() -> userService.createUser(userDto))
				.isInstanceOf(BadRequestException.class)
				.hasMessageContaining("Password must have a minimum length of");
	}

	@Test
	void testCreateUserPasswordTooShortThrowsException() {
		UserRepository userRepository = mock(UserRepository.class);

		UserService userService = new UserService(userRepository, passwordEncoder, modelMapper, roleService);

		UserDto userDto = new UserDto("username", "s");
		assertThatThrownBy(() -> userService.createUser(userDto))
				.isInstanceOf(BadRequestException.class)
				.hasMessageContaining("Password must have a minimum length of");
	}

	@Test
	void testCreateFirstUserIsAdmin() {
		UserRepository userRepository = mock(UserRepository.class);
		when(userRepository.save(any())).thenAnswer(e -> e.getArgument(0));

		UserService userService = new UserService(userRepository, passwordEncoder, modelMapper, roleService);

		UserDto userDto = new UserDto("username", "password123");
		userService.createUser(userDto);

		ArgumentCaptor<UserEntity> argumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
		verify(userRepository, times(1)).save(argumentCaptor.capture());
		assertThat(argumentCaptor.getValue().getRoles()).map(RoleEntity::getRole).containsExactlyInAnyOrder(Role.values());
	}

	@Test
	void testCreateSuccessiveUsersAreNotAdmins() {
		UserRepository userRepository = mock(UserRepository.class);
		when(userRepository.save(any())).thenAnswer(e -> e.getArgument(0));
		when(userRepository.count()).thenReturn(1L);

		UserService userService = new UserService(userRepository, passwordEncoder, modelMapper, roleService);

		UserDto userDto = new UserDto("username", "password123");
		userService.createUser(userDto);

		ArgumentCaptor<UserEntity> argumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
		verify(userRepository, times(1)).save(argumentCaptor.capture());
		assertThat(argumentCaptor.getValue().getRoles()).map(RoleEntity::getRole).containsOnly(Role.USER);
	}

	@Test
	void testCreateUserAlreadyExists() {
		UserRepository userRepository = mock(UserRepository.class);
		when(userRepository.save(any())).thenAnswer(e -> e.getArgument(0));
		when(userRepository.findByUsername(any())).thenReturn(Optional.of(new UserEntity()));

		UserService userService = new UserService(userRepository, passwordEncoder, modelMapper, roleService);

		UserDto userDto = new UserDto("username", "password123");

		assertThatThrownBy(() -> userService.createUser(userDto))
				.isInstanceOf(ConflictException.class)
				.hasMessageContaining("Choose different username");
	}
}
