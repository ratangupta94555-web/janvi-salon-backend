package com.janvo.salon.exceptions;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.janvo.salon.dtos.ApiErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex) {

		ApiErrorResponse response = ApiErrorResponse.builder().status(HttpStatus.NOT_FOUND.value())
				.message(ex.getMessage()).timestamp(LocalDateTime.now()).build();

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ApiErrorResponse> handleDuplicate(DuplicateResourceException ex) {

		ApiErrorResponse response = ApiErrorResponse.builder().status(HttpStatus.CONFLICT.value())
				.message(ex.getMessage()).timestamp(LocalDateTime.now()).build();

		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.joining(", "));

		ApiErrorResponse response = ApiErrorResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message(message)
				.timestamp(LocalDateTime.now()).build();

		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(SlotNotAvailableException.class)
	public ResponseEntity<ApiErrorResponse> handleSlotNotAvailable(SlotNotAvailableException ex) {

		ApiErrorResponse response = ApiErrorResponse.builder().status(HttpStatus.CONFLICT.value())
				.message(ex.getMessage()).timestamp(LocalDateTime.now()).build();

		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}
}