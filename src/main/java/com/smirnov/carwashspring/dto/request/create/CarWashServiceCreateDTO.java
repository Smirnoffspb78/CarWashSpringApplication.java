package com.smirnov.carwashspring.dto.request.create;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * @param name  Название услуги.
 * @param price Стоимость, [рубли].
 * @param time  Базовое время выполнения, [минуты].
 */
public record CarWashServiceCreateDTO(
        @Pattern(regexp = "[A-zА-я\"\\d][A-zА-я,.\"% \\d]{0,199}",
                message = """
                        name не должен быть пустым, длина должна быть от 1 до 200 символов.
                        Должен начинаться с символов A-zА-я\" или цифрового симвмвола.
                        Может иметь в составе названия символы A-zА-я,.\"%, пробельные символы и цифровые символы.
                        """) String name,
        @NotNull(message = "price не должен быть null") @Positive(message = "price должен быть положительным") BigDecimal price,
        @Positive int time) {

}
