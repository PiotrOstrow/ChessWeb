package com.github.piotrostrow.chess.controller;

import com.github.piotrostrow.chess.entity.UserEntity;
import com.github.piotrostrow.chess.serivce.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	public ResponseEntity<UserEntity> createUser(@RequestBody UserEntity user) {
		userService.createUser(user);
		return ResponseEntity.created(URI.create("/")).body(user);
	}

	@GetMapping
	public ResponseEntity<Iterable<UserEntity>> getAllUsers() {
		return ResponseEntity.ok(userService.getAllUsers());
	}
}
