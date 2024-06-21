package com.smirnov.carwashspring.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

/**
 * Пользователь.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
public class User {


    /**
     * Уровни доступа.
     */
    public enum Role {
        USER, ADMIN, OPERATOR
    }

    /**
     * Идентификатор пользователя.
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
     * Имя.
     */
    @Pattern(regexp = "[A-ZА-Я][A-zА-я-]{0,199}", message = "Имя должно начинаться с заглавной латинской или русской буквы. Имя может содержать символ \"-\"")
    @Column(name = "name")
    private String name;

    /**
     * E-mail.
     */
    @NotBlank(message = "email не должен быть пустым и иметь хотя бы один непробельный символ")
    @Column(name = "email")
    private String email;

    /**
     * Роль.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role = Role.USER;

    /**
     * Персональная скидка, [%].
     */
    @Range(min = 0, max = 100, message = "discount должен быть в диапазаоне от 0 до 100")
    @Column(name = "discount", insertable = false)
    private int discount = 0;

    /**
     * Аккаунт удален.
     */
    @Column(name = "is_delete", insertable = false)
    private boolean deleted = false;

    /**
     * Минимальная скидка, предоставляемая пользователям.
     */

    @Range(min = 0, max = 100, message = "minDiscountForUsers должен быть в диапазоне от 0 до 100 включительно")
    @Column(name = "min_discount_for_user", insertable = false)
    private int minDiscountForUsers = 0;

    /**
     * Максимальная скидка, предоставляемая пользователям.
     */
    @Range(min = 0, max = 100, message = "maxDiscountForUsers должен быть в диапазоне от 0 до 100 включительно")
    @Column(name = "max_discount_for_user", insertable = false)
    private int maxDiscountForUsers = 0;
}
