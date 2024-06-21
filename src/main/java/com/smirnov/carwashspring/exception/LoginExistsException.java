package com.smirnov.carwashspring.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginExistsException extends RuntimeException {
    private final String message;
}
