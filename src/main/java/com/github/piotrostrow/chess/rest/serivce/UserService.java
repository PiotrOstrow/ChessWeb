package com.github.piotrostrow.chess.rest.serivce;

import com.github.piotrostrow.chess.entity.UserEntity;
import com.github.piotrostrow.chess.repository.UserRepository;
import com.github.piotrostrow.chess.rest.dto.UserDto;
import com.github.piotrostrow.chess.rest.exception.BadRequestException;
import com.github.piotrostrow.chess.rest.exception.ConflictException;
import com.github.piotrostrow.chess.rest.exception.NotFoundException;
import com.github.piotrostrow.chess.security.Role;
import com.github.piotrostrow.chess.util.Util;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.piotrostrow.chess.util.Util.toSet;
import static org.springframework.util.StringUtils.hasLength;

@Service
public class UserService {

	private static final int MIN_PASSWORD_LENGTH = 6;

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper modelMapper;
	private final RoleService roleService;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, RoleService roleService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.modelMapper = modelMapper;
		this.roleService = roleService;
	}

	public UserDto createUser(UserDto userDto) {
		validate(userDto);

		UserEntity userEntity = new UserEntity(userDto.getUsername(), userDto.getEmail());
		userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));

		if (isFirstUser()) {
			userEntity.setRoles(toSet(roleService.getAllRoles()));
		} else {
			userEntity.setRoles(Set.of(roleService.getRole(Role.USER)));
		}

		return modelMapper.map(userRepository.save(userEntity), UserDto.class);
	}

	private void validate(UserDto userDto) {
		if (!hasLength(userDto.getPassword()) || userDto.getPassword().length() < MIN_PASSWORD_LENGTH) {
			throw new BadRequestException("Password must have a minimum length of " + MIN_PASSWORD_LENGTH);
		}

		if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
			throw new ConflictException("Choose different username");
		}
	}

	private boolean isFirstUser() {
		return userRepository.count() == 0;
	}

	public Collection<UserDto> getAllUsers() {
		return Util.stream(userRepository.findAll())
				.map(e -> modelMapper.map(e, UserDto.class))
				.collect(Collectors.toList());
	}

	public UserDto getUserByUsername(String username) {
		return userRepository.findByUsername(username)
				.map(entity -> modelMapper.map(entity, UserDto.class))
				.orElseThrow(() -> new NotFoundException("User by name \"" + username + "\" not found"));
	}

	public void deleteUser(String username) {
		UserEntity userEntity = userRepository.findByUsername(username)
				.orElseThrow(() -> new NotFoundException("User by name \"" + username + "\" not found"));
		userRepository.delete(userEntity);
	}
}
