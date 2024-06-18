package com.smirnov.carwashspring.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

/**
 * Бокс.
 */
@Getter
@Setter
@Entity
@Table(name = "boxes")
public class Box {

    /**
     * Идентификатор.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Время начала работы.
     */
    @NotNull(message = "start не должен быть null")
    @Column(name = "start")
    private LocalTime start;

    /**
     * Время окончания работы.
     */
    @NotNull(message = "finish не должен быть null")
    @Column(name = "finish")
    private LocalTime finish;

    /**
     * Коэффициент использования.
     */
    @Positive(message = "usageRate должен быть положительным")
    @Column(name = "usage_rate")
    private float usageRate;

    /**
     * Пользователь с ролью оператора.
     */
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id")
    @NotNull(message = "user не должен быть null")
    private User user;
}
