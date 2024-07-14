package com.smirnov.carwashspring.exception;

public class ForbiddenAccessException extends RuntimeException {

    private static final String MESSAGE =  "For user with ID %s is forbidden access";

    public ForbiddenAccessException(Number id) {
        super(MESSAGE.formatted(id));
    }
}
