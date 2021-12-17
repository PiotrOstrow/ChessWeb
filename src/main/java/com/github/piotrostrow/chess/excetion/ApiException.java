package com.github.piotrostrow.chess.excetion;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

	private final HttpStatus httpMethod;

	public ApiException(HttpStatus httpMethod, String message) {
		super(message);
		this.httpMethod = httpMethod;
	}

	public HttpStatus getStatus() {
		return httpMethod;
	}
}
