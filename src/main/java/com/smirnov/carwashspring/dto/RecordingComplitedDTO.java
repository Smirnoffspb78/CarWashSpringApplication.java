package com.smirnov.carwashspring.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Возврвщает список список предоставленных услуг, их стоимости и базового времени исполнение
 * @param id Идентификатор записи
 * @param timeComplite Время выполнения
 * @param cost Стоимость услуг
 * @param servicesName Список предоставленных услуг
 */
@Builder
public record RecordingComplitedDTO (
        @NotNull(message = "id не должен быть null") Integer id,
        long timeComplite,
        @Positive(message = "cost должен быть положительным") BigDecimal cost,
        @NotEmpty(message = "services не должен быть пустым") Set<String> servicesName
){
}
