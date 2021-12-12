package com.github.piotrostrow.chess.controller;

import com.github.piotrostrow.chess.dto.UserDto;
import com.github.piotrostrow.chess.entity.UserEntity;
import com.github.piotrostrow.chess.serivce.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	public ResponseEntity<UserDto> createUser(@RequestBody UserDto user) {
		userService.createUser(user);
		return ResponseEntity.created(URI.create("/")).body(user);
	}

	@GetMapping
	public ResponseEntity<Iterable<UserEntity>> getAllUsers() {
		return ResponseEntity.ok(userService.getAllUsers());
	}

	@GetMapping("{username}")
	public ResponseEntity<UserDto> getUser(@PathVariable String username) {
		return userService.getUserByUsername(username)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}
}
