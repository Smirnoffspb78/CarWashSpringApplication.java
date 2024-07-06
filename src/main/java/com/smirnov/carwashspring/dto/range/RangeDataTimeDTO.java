package com.smirnov.carwashspring.dto.range;

import com.smirnov.carwashspring.validation.RangeDateOrTime;

import java.time.LocalDateTime;

/**
 * DTO объект для передачи диапазона периода.
 *
 * @param start  Начало периода.
 * @param finish Окончание периода.
 */
@RangeDateOrTime
public record RangeDataTimeDTO(LocalDateTime start, LocalDateTime finish) implements Range {
}
