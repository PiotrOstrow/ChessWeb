package com.github.piotrostrow.chess.rest.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

	private final HttpStatus httpStatus;

	public ApiException(HttpStatus httpStatus, String message) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public HttpStatus getStatus() {
		return httpStatus;
	}
}
