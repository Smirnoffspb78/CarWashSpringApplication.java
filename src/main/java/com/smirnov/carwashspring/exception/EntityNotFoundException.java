package com.smirnov.carwashspring.exception;


public class  EntityNotFoundException extends RuntimeException {

    private final String message;

    public EntityNotFoundException(Class<?> entityClass, Number id) {
        message = entityClass.getSimpleName() + " with id " + id + " not found";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
