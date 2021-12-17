package com.github.piotrostrow.chess.rest.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

	@ExceptionHandler(TransactionSystemException.class)
	public ResponseEntity<Object> handleConstraintViolation(TransactionSystemException e) {
		Throwable originalException = e.getRootCause();
		if (originalException instanceof ConstraintViolationException) {
			String error = ((ConstraintViolationException) originalException).getConstraintViolations().stream()
					.map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
					.findFirst().orElseThrow();

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", error));
		}

		LOGGER.error("Could not handle TransactionSystemException", e);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<Object> handleApiException(ApiException e) {
		// TODO: dto on all of these
		return ResponseEntity.status(e.getStatus()).body(Map.of("error", e.getMessage()));
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Malformed request body"));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleAll(Exception e) {
		LOGGER.warn("Unhandled exception {}", e.getClass().getSimpleName(), e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
}
