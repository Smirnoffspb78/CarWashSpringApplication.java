package com.smirnov.carwashspring.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
public class RecordingDTO {

    /**
     * Идентификатор записи.
     */
    @Positive(message = "id<0")
    @NotNull(message = "id is null")
    private Integer id;

    /**
     * Пользователь.
     */
    @NotNull(message = "Пользователь не должен быть null")
    @Positive(message = "IdUser<0")
    private Integer idUser;

    /**
     * Дата начала предоставления услуг.
     */
    @NotNull(message = "startDate не должна быть null")
    private LocalDateTime start;


    /**
     * Дата окончания предоставления услуг.
     */
    @NotNull(message = "finish_date не должна быть null")
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
     * Сотоимсоть услуг с учетом скидки
     */
    @Positive(message = "cost должен быть положительным")
    private BigDecimal cost;

    /**
     * Бокс.
     */
    @NotNull(message = "IdBox не должен быть null")
    @Positive(message = "IdBox должен быть положительным")
    private Integer idBox;

    /**
     * Список выбранных услуг.
     */
    private Set<Integer> idServices = new HashSet<>();
}
