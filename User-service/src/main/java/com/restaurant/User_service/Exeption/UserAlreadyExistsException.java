package com.restaurant.User_service.Exeption;

public class UserAlreadyExistsException extends RuntimeException {
	public UserAlreadyExistsException(String message) {
        super(message);
    }
}
