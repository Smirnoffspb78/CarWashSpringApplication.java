package com.smirnov.carwashspring.entity.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Услуги автомойки.
 */
@Getter
@Setter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "services")
@ToString
public class CarWashService {
    /**
     * Идентификатор.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @JsonIgnore
    private Integer id;
    /**
     * Название услуги.
     */
    @Column(name = "name")
    @Pattern(regexp = "[A-zА-я\"\\d][A-zА-я,.\"% \\d]{0,199}",
            message = """
                    name не должен быть пустым, длина должна быть от 1 до 200 символов.
                    Должен начинаться с символов A-zА-я\" или цифрового симвмвола.
                    Может иметь в составе названия символы A-zА-я,.\"%, пробельные символы и цифровые символы.
                    """)
    private String name;

    /**
     * Стоимость, [рубли].
     */
    @NotNull(message = "price не должен быть null")
    @Positive(message = "price должен быть положительным")
    @Column(name = "price")
    private BigDecimal price;

    /**
     * Базовое время выполнения, [минуты].
     */
    @Positive
    @Column(name = "time")
    private int time;

}
