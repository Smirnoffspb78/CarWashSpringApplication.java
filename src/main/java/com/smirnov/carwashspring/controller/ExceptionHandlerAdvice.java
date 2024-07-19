package com.smirnov.carwashspring.controller;

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
    public String entityException(EntityNotFoundException e) {
        log.error("{}. ", HttpStatus.NOT_FOUND, e);
        return responseServer(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler({LoginException.class, RecordingCreateException.class, IllegalArgumentException.class, JWTValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String loginException(LoginException e) {
        log.error("{}. ", HttpStatus.BAD_REQUEST, e);
        return responseServer(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(ForbiddenAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String forbiddenException(ForbiddenAccessException e) {
        log.error("{}. ", HttpStatus.FORBIDDEN, e);
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
        log.error("{}. {}", HttpStatus.BAD_REQUEST, stringBuilder);
        return responseServer(HttpStatus.BAD_REQUEST, stringBuilder.toString());
    }

    private String responseServer(HttpStatus httpStatus, String massage) {
        return "%s %n%s".formatted(httpStatus, massage);
    }
}
