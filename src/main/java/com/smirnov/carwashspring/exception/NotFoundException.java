package com.smirnov.carwashspring.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public abstract class NotFoundException extends RuntimeException {
    private String message;
}
