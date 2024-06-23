package com.smirnov.carwashspring.exception;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServiceNotFoundException extends NotFoundException {
    public ServiceNotFoundException(String message) {
        super(message);
    }
}
