package com.github.piotrostrow.chess.serivce;

import com.github.piotrostrow.chess.entity.UserEntity;
import com.github.piotrostrow.chess.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public UserEntity createUser(UserEntity userEntity) {
		userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
		return userRepository.save(userEntity);
	}

	public Iterable<UserEntity> getAllUsers() {
		return userRepository.findAll();
	}
}
