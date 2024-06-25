package com.smirnov.carwashspring.dto.response.get;

import com.smirnov.carwashspring.entity.service.CarWashService;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @param id       Идентификтаор записи.
 * @param start    Дата начала предоставления услуг.
 * @param finish   Дата окончания предоставления услуг.
 * @param cost     Сотоимсоть услуг с учетом скидки
 * @param boxId    Бокс.
 * @param services Список выбранных услуг.
 */
@Builder
public record RecordingReservedDTO(
        @NotNull(message = "id не должен быть null") Integer id,
        @NotNull(message = "startDate не должна быть null") @Future(message = "start_date должна быть в будущем") LocalDateTime start,
        @NotNull(message = "finish_date не должна быть null") @Future(message = "finish_date должна быть в будущем") LocalDateTime finish,
        @Positive(message = "cost должен быть положительным") BigDecimal cost,
        @NotNull(message = "Бокс не должен быть null") Integer boxId,
        @NotEmpty(message = "services не должен быть пустым") Set<CarWashService> services) {
}
