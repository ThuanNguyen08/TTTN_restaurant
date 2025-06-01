package com.restaurant.User_service.Exeption;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(),
				LocalDateTime.now());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage(),
				LocalDateTime.now());
		return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(UnauthorizedAccessException.class)
	public ResponseEntity<ErrorResponse> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage(),
				LocalDateTime.now());
		return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(TokenRefreshException.class)
	public ResponseEntity<ErrorResponse> handleTokenRefreshException(UnauthorizedAccessException ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage(),
				LocalDateTime.now());
		return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
	}

	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(),
				LocalDateTime.now());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.TOO_MANY_REQUESTS.value(), ex.getMessage(),
				LocalDateTime.now());
		return new ResponseEntity<>(errorResponse, HttpStatus.TOO_MANY_REQUESTS);
	}
	
	// Xử lý lỗi validation từ @Valid
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Dữ liệu không hợp lệ");

		// Lấy tất cả các lỗi validation
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errorResponse.addValidationError(fieldName, errorMessage);
		});

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

}
