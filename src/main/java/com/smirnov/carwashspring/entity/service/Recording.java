package com.smirnov.carwashspring.entity.service;

import com.smirnov.carwashspring.entity.groupvalidated.RangeDateTimeGroupValidation;
import com.smirnov.carwashspring.entity.users.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Запись.
 */
@Entity
@Getter
@Setter
@Table(name = "records")
@ToString
public class Recording {
    /**
     * Идентификатор записи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Пользователь.
     */
    @NotNull(message = "Пользователь не должен быть null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Дата начала предоставления услуг.
     */
    @NotNull(message = "start не должнен быть null")
    @Future(message = "start должна быть в будущем", groups = RangeDateTimeGroupValidation.class)
    @Column(name = "start")
    private LocalDateTime start;


    /**
     * Дата окончания предоставления услуг.
     */
    @NotNull(message = "finish не должна быть null")
    @Future(message = "finish должен быть в будущем", groups = RangeDateTimeGroupValidation.class)
    @Column(name = "finish")
    private LocalDateTime finish;

    /**
     * Дата и время забронированы.
     */
    @Column(name = "is_reserve", insertable = false)
    private boolean reserved = true;

    /**
     * Услуги предоставлены.
     */
    @Column(name = "is_complite", insertable = false)
    private boolean complited = false;

    /**
     * Сотоимсоть услуг с учетом скидки
     */
    @Column(name = "cost")
    @Positive(message = "cost должен быть положительным")
    private BigDecimal cost;

    /**
     * Бокс.
     */
    @NotNull(message = "Бокс не должен быть null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "box_id")
    private Box box;

    /**
     * Список выбранных услуг.
     */
    @NotEmpty(message = "services не должен быть пустым")
    @ManyToMany
    @JoinTable(name = "service_record",
            joinColumns = @JoinColumn(name = "record_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id"))
    private Set<CarWashService> services = new HashSet<>();
}