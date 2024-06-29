package com.smirnov.carwashspring.dto.request.create;

import com.smirnov.carwashspring.dto.range.Range;
import com.smirnov.carwashspring.validation.RangeDateOrTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import lombok.Setter;

import java.time.LocalTime;


/**
 *
 */
@RangeDateOrTime
@Getter
@Setter
public class BoxCreateDTO implements Range {
    /**
     * Время начала работы.
     */
    private @NotNull(message = "start не должен быть null") LocalTime start;
    /**
     * Время окончания работы.
     */
    private @NotNull(message = "finish не должен быть null") LocalTime finish;
    /**
     * Коэффициент использования.
     */
    private @Positive(message = "usageRate должен быть положительным") float usageRate;
    /**
     * Пользователь с ролью оператора.
     */
    private @NotNull(message = "operatorId не должен быть null") Integer operatorId;
}
