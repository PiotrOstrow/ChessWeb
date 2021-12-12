package com.github.piotrostrow.chess.serivce;

import com.github.piotrostrow.chess.dto.UserDto;
import com.github.piotrostrow.chess.entity.UserEntity;
import com.github.piotrostrow.chess.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper modelMapper;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.modelMapper = modelMapper;
	}

	public UserDto createUser(UserDto userDto) {
		UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
		userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
		return modelMapper.map(userRepository.save(userEntity), UserDto.class);
	}

	public Iterable<UserEntity> getAllUsers() {
		return userRepository.findAll();
	}

	public Optional<UserDto> getUserByUsername(String username) {
		return userRepository.findByUsername(username).map(entity -> modelMapper.map(entity, UserDto.class));
	}
}
