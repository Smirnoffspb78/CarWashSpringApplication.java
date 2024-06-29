package com.smirnov.carwashspring.dto.response.get;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
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
    @NotNull(message = "id is null")
    Integer id;

    /**
     * Пользователь.
     */
    @NotNull(message = "Пользователь не должен быть null")
    private Integer idUser;
    /**
     * Дата начала предоставления услуг.
     */
    @NotNull(message = "startDate не должна быть null")
    private  LocalDateTime start;
    private  @NotNull(message = "finish_date не должна быть null") LocalDateTime finish;
    private  boolean reserved;
    private  boolean complited;
    private  @Positive(message = "cost должен быть положительным") BigDecimal cost;
    private  @NotNull(message = "IdBox не должен быть null")
    @Positive(message = "IdBox должен быть положительным") Integer idBox;
    private Set<Integer> idServices = new HashSet<>();

    /**
     * @param id
     * @param idUser
     * @param start
     * @param finish     Дата окончания предоставления услуг.
     * @param reserved   Дата и время забронированы.
     * @param complited  Услуги предоставлены.
     * @param cost       Сотоимсоть услуг с учетом скидки
     * @param idBox      Бокс.
     * @param idServices Список выбранных услуг.
     */
}
