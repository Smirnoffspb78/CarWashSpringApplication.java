package com.smirnov.carwashspring.exception;

public class JWTValidException extends RuntimeException {

    public JWTValidException(String message) {
        super(message);
    }
}
