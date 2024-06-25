package com.smirnov.carwashspring.dto.request.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO для передачи данных пользователя.
 *
 * @param login    Логин.
 * @param password Пароль.
 * @param name     Имя.
 * @param email    E-mail.
 */
public record UserCreateDTO(
        @Pattern(regexp = "[A-z\\d][A-z\\d.]{3,199}",
                message = "Логин должен содержать латинские буквы, цифры или символ \".\". Длина логины должна иметь хотябы три символ. Логин не может начинаться с \".\"")
        String login,
        @Pattern(regexp = "[A-z\\d!#$*]{8,200}",
                message = "password может содержать латинские буквы, цифры и символы !#$*. Длин должны быть от 8 до 200 символов")
        String password,
        @Pattern(regexp = "[A-ZА-Я][A-zА-я-]{0,199}", message = "Имя должно начинаться с заглавной латинской или русской буквы. Имя может содержать символ \"-\"")
        String name,
        @NotBlank(message = "email не должен быть пустым и иметь хотя бы один непробельный символ")
        String email) {
}
