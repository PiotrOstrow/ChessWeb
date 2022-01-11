package com.github.piotrostrow.chess.rest.controller;

import com.github.piotrostrow.chess.rest.dto.UserDto;
import com.github.piotrostrow.chess.rest.serivce.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	public ResponseEntity<UserDto> createUser(@RequestBody UserDto user) {
		return ResponseEntity.created(URI.create("/" + user.getUsername())).body(userService.createUser(user));
	}

	@GetMapping
	public ResponseEntity<Object> getAllUsers() {
		return ResponseEntity.ok(Map.of("users", userService.getAllUsers()));
	}

	@GetMapping("{username}")
	public ResponseEntity<UserDto> getUser(@PathVariable String username) {
		return ResponseEntity.ok(userService.getUserByUsername(username));
	}

	@DeleteMapping("{username}")
	public ResponseEntity<Object> deleteUser(@PathVariable String username) {
		userService.deleteUser(username);
		return ResponseEntity.noContent().build();
	}
}
