package com.smirnov.carwashspring.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @param id         Идентификатор записи.
 * @param idUser     Пользователь.
 * @param start      Дата начала предоставления услуг.
 * @param finish     Дата окончания предоставления услуг.
 * @param reserved   Дата и время забронированы.
 * @param complited  Услуги предоставлены.
 * @param cost       Сотоимсоть услуг с учетом скидки
 * @param idBox      Бокс.
 * @param idServices Список выбранных услуг.
 */

@Builder
public record RecordingDTO(@Positive(message = "id<0") @NotNull(message = "id is null") Integer id,
                           @NotNull(message = "Пользователь не должен быть null") @Positive(message = "IdUser<0") Integer idUser,
                           @NotNull(message = "startDate не должна быть null") LocalDateTime start,
                           @NotNull(message = "finish_date не должна быть null") LocalDateTime finish, boolean reserved,
                           boolean complited, @Positive(message = "cost должен быть положительным") BigDecimal cost,
                           @NotNull(message = "IdBox не должен быть null") @Positive(message = "IdBox должен быть положительным") Integer idBox,
                           Set<Integer> idServices) {

}
