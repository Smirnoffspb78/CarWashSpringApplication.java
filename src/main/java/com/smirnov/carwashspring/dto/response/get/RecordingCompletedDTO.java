package com.smirnov.carwashspring.dto.response.get;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Содержит список предоставленных услуг, их стоимости и времени исполнение.
 */
@Getter
@Setter
@Builder
public class RecordingCompletedDTO {
    /**
     * Идентификатор записи.
     */
    private Integer id;
    /**
     * Время выполнения.
     */
    private long timeComplete;
    /**
     * Стоимость услуг.
     */
    private BigDecimal cost;
    /**
     * Список предоставленных услуг.
     */
    private Set<String> servicesName;
}
