package com.smirnov.carwashspring.entity.users;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Аккаунт.
 */
@Entity
@Table(name = "accounts")
@Getter
@Setter
@ToString
public class Account {

    /**
     * Идентификатор аккаунта.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Логин.
     */
    @Pattern(regexp = "[A-z\\d][A-z\\d.]{3,199}",
            message = "Логин должен содержать латинские буквы, цифры или символ \".\". Длина логины должна иметь хотябы три символ. Логин не может начинаться с \".\"")
    @Column(name = "login", updatable = false)
    private String login;

    /**
     * Пароль.
     */
    @Pattern(regexp = "[A-z\\d!#$*]{8,200}",
            message = "password может содержать латинские буквы, цифры и символы !#$*. Длин должны быть от 8 до 200 символов")
    @Column(name = "password")
    private String password;

    /**
     * Пользователь.
     */
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @NotNull
    private User user;
}
