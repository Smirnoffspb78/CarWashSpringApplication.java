package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.dto.request.create.UserCreateDTO;
import com.smirnov.carwashspring.dto.response.get.Token;
import com.smirnov.carwashspring.service.security.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.security.auth.login.AccountNotFoundException;


/**
 * Контроллер аккаунта.
 */
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    /**
     * Сервисный слой аккаунта.
     */
    private final AccountService accountService;

    /**
     * Регистрирует нового пользователя.
     * @param user Пользователь
     * @return Идентификатор пользователя.
     */
    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public Integer createAccount(@RequestBody @Valid UserCreateDTO user) {
        log.info("POST: /account/registration");
            return accountService.registration(user);
    }

    /**
     * Авторизует пользователя.
     * @param login Логин
     * @param password Пароль
     * @return Токен пользователя
     */
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public Token loginAccount(@RequestParam("login") String login,
                              @RequestParam("password") String password) {
        try {
            log.info("POST: /account/login");
            return accountService.loginAccount(login, password);
        } catch (AccountNotFoundException e) {
            log.error("Введены неверные логин или пароль");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
