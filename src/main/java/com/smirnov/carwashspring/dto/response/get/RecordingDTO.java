package com.smirnov.carwashspring.dto.response.get;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * DTO для получения информации о записи.
 */

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class RecordingDTO {
    /**
     * Идентификатор записи.
     */
    private Integer id;

    /**
     * Пользователь.
     */
    private Integer idUser;
    /**
     * Дата начала предоставления услуг.
     */
    private LocalDateTime start;

    /**
     * Дата окончания предоставления услуг.
     */
    private LocalDateTime finish;
    /**
     * Дата и время забронированы.
     */
    private boolean reserved;
    /**
     * Услуги предоставлены.
     */
    private boolean complited;
    /**
     * Стоимость услуг с учетом скидки.
     */
    private BigDecimal cost;
    /**
     * Идентификатор бокса
     */
    private Integer idBox;
    /**
     * Список выбранных услуг.
     */
    private Set<Integer> idServices = new HashSet<>();
}
