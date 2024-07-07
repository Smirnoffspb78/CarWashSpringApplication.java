package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.exception.EntityNotFoundException;
import com.smirnov.carwashspring.exception.ForbiddenAccessException;
import com.smirnov.carwashspring.exception.LoginException;
import com.smirnov.carwashspring.exception.LoginNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class ExceptionController {

    @ResponseBody
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String entityException(EntityNotFoundException e) {
        log.error("{}. {}", HttpStatus.NOT_FOUND, e.getMessage());
        return HttpStatus.NOT_FOUND + e.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(LoginNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String loginNotFoundException(LoginNotFoundException e) {
        log.error("{}. {}", HttpStatus.NOT_FOUND, e.getMessage());
        return HttpStatus.NOT_FOUND + e.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(LoginException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String loginException(LoginException e) {
        log.error("{}. {}", HttpStatus.BAD_REQUEST, e.getMessage());
        return HttpStatus.BAD_REQUEST + e.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String userNotFoundException(UsernameNotFoundException e) {
        log.error("{}. {}", HttpStatus.NOT_FOUND, e.getMessage());
        return HttpStatus.NOT_FOUND + e.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ForbiddenAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String forbiddenException(ForbiddenAccessException e) {
        log.error("{}. {}", HttpStatus.FORBIDDEN, e.getMessage());
        return HttpStatus.FORBIDDEN + e.getMessage();
    }
}
