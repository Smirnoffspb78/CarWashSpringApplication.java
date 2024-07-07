package com.smirnov.carwashspring.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ForbiddenAccessException extends RuntimeException {

    private final Number id;
    @Override
    public String getMessage() {
        return "For user with ID " + id + " is forbidden access";
    }
}
