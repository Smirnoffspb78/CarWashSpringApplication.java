package com.smirnov.carwashspring.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO объект для передаче диапиазона периода.
 *
 * @param start  Начало перода.
 * @param finish Окончание перода.
 */
public record RangeTimeDTO(@NotNull(message = "start не должен быть null") LocalDateTime start,
                           @NotNull(message = "finish не должен быть null") LocalDateTime finish) {
}
