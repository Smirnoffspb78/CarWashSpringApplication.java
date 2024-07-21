package com.smirnov.carwashspring.dto.response.get;


import lombok.*;


import java.math.BigDecimal;

/**
 * DTO для получения информации об услуге
 */
@Getter
@Setter
@ToString
public class CarWashServiceDTO {
    /**
     * Название услуги.
     */
    private String name;
    /**
     * price Стоимость, [рубли].
     */
    private BigDecimal price;
    /**
     * Базовое время выполнения, [минуты].
     */
    private int time;
}
