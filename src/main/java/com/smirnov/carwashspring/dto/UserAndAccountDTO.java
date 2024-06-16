package com.smirnov.carwashspring.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для передачи данных пользователя.
 */
@Getter
@Setter
public class UserAndAccountDTO {

    /**
     * Логин.
     */
    @Column(name = "login", updatable = false)
    private String login;

    /**
     * Пароль.
     */
    @Column(name = "password")
    private String password;

    /**
     * Имя.
     */
    @Column(name = "name")
    private String name;

    /**
     * E-mail.
     */
    @Column(name = "email")
    private String email;
}
