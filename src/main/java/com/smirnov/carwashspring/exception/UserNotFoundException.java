package com.smirnov.carwashspring.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
