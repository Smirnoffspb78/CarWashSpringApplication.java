package com.smirnov.carwashspring.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LoginNotFoundException extends RuntimeException {
    public LoginNotFoundException(String message) {
        super(message);
    }
}
