package com.smirnov.carwashspring.dto.response.get;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@Getter
@Setter
@NoArgsConstructor
public final class RecordingReservedDTO {
    /**
     * Идентификатор записи.
     */
    private Integer id;
    /**
     * Дата начала предоставления услуг.
     */
    private LocalDateTime start;

    /**
     * Дата окончания предоставления услуг.
     */
    private LocalDateTime finish;

    /**
     * Стоимость услуг с учетом скидки.
     */
    private BigDecimal cost;

    /**
     * Бокс.
     */
    private Integer boxId;
    /**
     * Список выбранных услуг.
     */
    private Set<String> servicesName = new HashSet<>();

}
