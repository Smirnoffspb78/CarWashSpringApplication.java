package com.smirnov.carwashspring.exception;

public class LoginException extends RuntimeException{
    public LoginException(String message) {
        super(message);
    }
}
