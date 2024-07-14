package com.smirnov.carwashspring.dto.response.get;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Токен пользователя.
 */
@Getter
@Setter
@ToString
public class Token {
    /**
     * Токен для получения данных.
     */
    private String accessToken;
    /**
     * Токен для получения нового accessToken.
     */
    private String refreshToken;
}
