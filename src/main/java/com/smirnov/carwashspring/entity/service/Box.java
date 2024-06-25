package com.smirnov.carwashspring.entity.service;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.smirnov.carwashspring.entity.users.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id")
    @NotNull(message = "user не должен быть null")
    private User user;

    @OneToMany (mappedBy = "box", fetch = FetchType.LAZY)
    private List<Recording> recordings = new ArrayList<>();
}
