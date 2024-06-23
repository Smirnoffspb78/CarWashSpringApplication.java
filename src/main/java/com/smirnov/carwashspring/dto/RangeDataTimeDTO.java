package com.smirnov.carwashspring.dto;

import com.smirnov.carwashspring.annotation.RangeLocalDateTime;
import java.time.LocalDateTime;
/**
 * DTO объект для передачи диапазона периода.
 *
 * @param start  Начало периода.
 * @param finish Окончание периода.
 */
@RangeLocalDateTime
public record RangeDataTimeDTO(LocalDateTime start,LocalDateTime finish) {
}
