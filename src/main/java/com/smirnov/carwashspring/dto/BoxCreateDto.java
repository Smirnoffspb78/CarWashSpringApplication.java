package com.smirnov.carwashspring.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalTime;

/**
 * @param start     Время начала работы.
 * @param finish    Время окончания работы.
 * @param usageRate Коэффициент использования.
 * @param userId    Пользователь с ролью оператора.
 */
public record BoxCreateDto(@NotNull(message = "start не должен быть null") LocalTime start,
                           @NotNull(message = "finish не должен быть null") LocalTime finish,
                           @Positive(message = "usageRate должен быть положительным") float usageRate,
                           @NotNull(message = "userId не должен быть null") Integer userId) {

}
