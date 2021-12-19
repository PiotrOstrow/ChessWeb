package com.github.piotrostrow.chess.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@JsonPropertyOrder({"status", "error", "message", "timestamp"})
public class ApiError {

	private final String error;
	private final String message;

	private final int status;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss.SSS")
	private final LocalDateTime timestamp;

	public ApiError(Exception exception, HttpStatus httpStatus) {
		this(exception.getMessage(), httpStatus);
	}

	public ApiError(String message, HttpStatus httpStatus) {
		this.error = httpStatus.name();
		this.message = message;
		this.status = httpStatus.value();
		this.timestamp = LocalDateTime.now();
	}

	public String getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public int getStatus() {
		return status;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}
}
