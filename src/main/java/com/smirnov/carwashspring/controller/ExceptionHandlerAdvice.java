package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.exception.DownloadFileException;
import com.smirnov.carwashspring.exception.EntityNotFoundException;
import com.smirnov.carwashspring.exception.ForbiddenAccessException;
import com.smirnov.carwashspring.exception.JWTValidException;
import com.smirnov.carwashspring.exception.LoginException;
import com.smirnov.carwashspring.exception.LoginNotFoundException;
import com.smirnov.carwashspring.exception.RecordingCreateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {

    @ResponseBody
    @ExceptionHandler({EntityNotFoundException.class, LoginNotFoundException.class, UsernameNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String entityException(RuntimeException e) {
        return responseServer(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler({LoginException.class, RecordingCreateException.class, IllegalArgumentException.class, JWTValidException.class, DownloadFileException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String badRequestException(RuntimeException e) {
        return responseServer(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(ForbiddenAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String forbiddenException(ForbiddenAccessException e) {
        return responseServer(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String notValidException(MethodArgumentNotValidException e) {
        StringBuilder stringBuilder = new StringBuilder();
        e.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .forEach(str -> stringBuilder.append("\n").append(str));
        return responseServer(HttpStatus.BAD_REQUEST, stringBuilder.toString());
    }

    private String responseServer(HttpStatus httpStatus, String message) {
        log.error("{}. {}", httpStatus, message);
        return "%s %n%s".formatted(httpStatus, message);
    }
}
