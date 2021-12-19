package com.github.piotrostrow.chess.rest.exception;

import com.github.piotrostrow.chess.rest.dto.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

import static org.springframework.http.HttpStatus.*;

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

			return ResponseEntity.status(BAD_REQUEST).body(new ApiError(error, BAD_REQUEST));
		}

		LOGGER.error("Could not handle TransactionSystemException", e);

		return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiError("TransactionSystemException", INTERNAL_SERVER_ERROR));
	}

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<Object> handleApiException(ApiException e) {
		return ResponseEntity.status(e.getStatus()).body(new ApiError(e, e.getStatus()));
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Object> handleAuthenticationException(AuthenticationException e) {
		return ResponseEntity.status(UNAUTHORIZED).body(new ApiError(e, UNAUTHORIZED));
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ResponseEntity.status(BAD_REQUEST).body(new ApiError("Malformed request body", BAD_REQUEST));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleAll(Exception e) {
		LOGGER.warn("Unhandled exception {}", e.getClass().getSimpleName(), e);
		return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiError(e, INTERNAL_SERVER_ERROR));
	}
}
