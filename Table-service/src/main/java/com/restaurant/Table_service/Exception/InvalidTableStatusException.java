package com.restaurant.Table_service.Exception;

public class InvalidTableStatusException extends RuntimeException {
    public InvalidTableStatusException(String message) {
        super(message);
    }
}