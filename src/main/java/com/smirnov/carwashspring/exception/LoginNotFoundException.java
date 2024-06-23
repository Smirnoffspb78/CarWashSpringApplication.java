package com.smirnov.carwashspring.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LoginNotFoundException extends NotFoundException {
    public LoginNotFoundException(String message) {
        super(message);
    }
}
