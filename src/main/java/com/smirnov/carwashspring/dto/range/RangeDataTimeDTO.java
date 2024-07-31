package com.smirnov.carwashspring.dto.range;

import com.smirnov.carwashspring.validation.RangeDateOrTime;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.Objects;

/**
 * DTO объект для передачи диапазона периода.
 */
@RangeDateOrTime
@Getter
@Setter
public class RangeDataTimeDTO implements SpecifyRange {
    /**
     * Начало периода.
     */
    private LocalDateTime start;
    /**
     * Окончание периода.
     */
    private LocalDateTime finish;

    @Override
    public int compare(Temporal o1, Temporal o2) {

        return 0;
    }
}
