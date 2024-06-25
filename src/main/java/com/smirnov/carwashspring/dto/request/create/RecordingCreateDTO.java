package com.smirnov.carwashspring.dto.request.create;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

/**DTO для создания записи.
 * @param idUser     Пользователь.
 * @param start      Дата начала предоставления услуг.
 * @param idServices Идентификаторы выбранных услуг.
 */
public record RecordingCreateDTO(
        @NotNull(message = "Пользователь не должен быть null") Integer idUser,
        @NotNull(message = "startDate не должна быть null")
        @Future(message = "Начало предоставления услуг должно быть в будущем") LocalDateTime start,
        @NotEmpty(message = "Список услуг не может быть null или быть пустым") Set<Integer> idServices) {
}
