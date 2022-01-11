package com.github.piotrostrow.chess.rest.serivce;

import com.github.piotrostrow.chess.entity.RoleEntity;
import com.github.piotrostrow.chess.entity.UserEntity;
import com.github.piotrostrow.chess.repository.UserRepository;
import com.github.piotrostrow.chess.rest.dto.UserDto;
import com.github.piotrostrow.chess.rest.exception.BadRequestException;
import com.github.piotrostrow.chess.rest.exception.ConflictException;
import com.github.piotrostrow.chess.rest.exception.NotFoundException;
import com.github.piotrostrow.chess.security.Role;
import org.junit.jupiter.api.BeforeEach;
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

	private final UserRepository userRepository = mock(UserRepository.class);

	private UserService userService;


	@BeforeEach
	void setUp() {
		userService = new UserService(userRepository, passwordEncoder, modelMapper, roleService);
	}

	@Test
	void testCreateUserNoPasswordThrowsException() {
		UserDto userDto = new UserDto();
		assertThatThrownBy(() -> userService.createUser(userDto))
				.isInstanceOf(BadRequestException.class)
				.hasMessageContaining("Password must have a minimum length of");
	}

	@Test
	void testCreateUserPasswordTooShortThrowsException() {
		UserDto userDto = new UserDto("username", "s");
		assertThatThrownBy(() -> userService.createUser(userDto))
				.isInstanceOf(BadRequestException.class)
				.hasMessageContaining("Password must have a minimum length of");
	}

	@Test
	void testCreateFirstUserIsAdmin() {
		when(userRepository.save(any())).thenAnswer(e -> e.getArgument(0));

		UserDto userDto = new UserDto("username", "password123");
		userService.createUser(userDto);

		ArgumentCaptor<UserEntity> argumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
		verify(userRepository, times(1)).save(argumentCaptor.capture());
		assertThat(argumentCaptor.getValue().getRoles()).map(RoleEntity::getRole).containsExactlyInAnyOrder(Role.values());
	}

	@Test
	void testCreateSuccessiveUsersAreNotAdmins() {
		when(userRepository.save(any())).thenAnswer(e -> e.getArgument(0));
		when(userRepository.count()).thenReturn(1L);

		UserDto userDto = new UserDto("username", "password123");
		userService.createUser(userDto);

		ArgumentCaptor<UserEntity> argumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
		verify(userRepository, times(1)).save(argumentCaptor.capture());
		assertThat(argumentCaptor.getValue().getRoles()).map(RoleEntity::getRole).containsOnly(Role.USER);
	}

	@Test
	void testCreateUserAlreadyExists() {
		when(userRepository.save(any())).thenAnswer(e -> e.getArgument(0));
		when(userRepository.findByUsername(any())).thenReturn(Optional.of(new UserEntity()));

		UserDto userDto = new UserDto("username", "password123");

		assertThatThrownBy(() -> userService.createUser(userDto))
				.isInstanceOf(ConflictException.class)
				.hasMessageContaining("Choose different username");
	}

	@Test
	void testGetUserByUsernameShouldReturnUserDto() {
		UserEntity user = new UserEntity("User123", "user@email.com");
		when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

		UserDto userDto = userService.getUserByUsername(user.getUsername());

		assertThat(userDto).isEqualTo(modelMapper.map(user, UserDto.class));
	}

	@Test
	void testGetUserByUsernameDoesNotExistShouldThrowNotFoundException() {
		assertThatThrownBy(() -> userService.getUserByUsername("user123")).isInstanceOf(NotFoundException.class);
	}

	@Test
	void testDeleteUserShouldRemoveFromRepository() {
		UserEntity user = new UserEntity("User123", "user@email.com");
		when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

		userService.deleteUser(user.getUsername());

		verify(userRepository, times(1)).delete(user);
	}

	@Test
	void testDeleteNonExistingUserShouldThrowNotFoundException() {
		assertThatThrownBy(() -> userService.deleteUser("user123")).isInstanceOf(NotFoundException.class);
	}
}
