package com.smirnov.carwashspring.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * DTO объект для передаче диапиазона периода.
 */
@Getter
@Setter
public class RangeForProfitDTO {

    /**
     * Начало перода.
     */
    @NotNull
    private LocalDateTime start;
    /**
     * Окончание перода.
     */
    @NotNull
    private LocalDateTime finish;
}
