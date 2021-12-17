package com.github.piotrostrow.chess.rest.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApiException {

	public BadRequestException(String message) {
		super(HttpStatus.BAD_REQUEST, message);
	}
}