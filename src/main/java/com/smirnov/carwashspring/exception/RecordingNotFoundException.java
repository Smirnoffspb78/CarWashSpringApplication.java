package com.smirnov.carwashspring.exception;

import lombok.NoArgsConstructor;


@NoArgsConstructor
public class RecordingNotFoundException extends NotFoundException {
    public RecordingNotFoundException(String message) {
        super(message);
    }
}
