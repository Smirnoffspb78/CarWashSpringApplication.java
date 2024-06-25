package com.smirnov.carwashspring.dto.response.get;


import lombok.Builder;


import java.math.BigDecimal;

/**
 * @param name  Название услуги.
 * @param price Стоимость, [рубли].
 * @param time  Базовое время выполнения, [минуты].
 */
@Builder
public record CarWashServiceDTO(String name, BigDecimal price, int time) {
}
