package com.smirnov.carwashspring.exception;


public class  EntityNotFoundException extends RuntimeException {

    private static final String MESSAGE = "%s with id %s not found";

    public EntityNotFoundException(Class<?> entityClass, Number id) {
        super(MESSAGE.formatted(entityClass.getSimpleName(), id));
    }
}
