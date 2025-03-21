package com.restaurant.User_service.Exeption;

public class UnauthorizedAccessException extends RuntimeException {
	public UnauthorizedAccessException(String message) {
        super(message);
    }
}
