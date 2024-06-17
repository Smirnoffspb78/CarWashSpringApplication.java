package com.smirnov.carwashspring.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO для передачи данных пользователя.
 *
 * @param login    Логин.
 * @param password Пароль.
 * @param name     Имя.
 * @param email    E-mail.
 */
public record UserCreateDTO(@NotNull(message = "login is null") String login,
                            @NotNull(message = "password is null") String password,
                            @NotNull(message = "name is null") String name,
                            @NotNull(message = "email is null") String email) {
}
