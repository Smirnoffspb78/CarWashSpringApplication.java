package com.smirnov.carwashspring.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BoxNotFountException extends NotFoundException {
    public BoxNotFountException(String message) {
        super(message);
    }
}
