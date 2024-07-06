package com.smirnov.carwashspring.entity.users;


import com.smirnov.carwashspring.entity.service.Recording;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import java.util.ArrayList;
import java.util.List;

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
     * Идентификатор пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Логин.
     */
    @Pattern(regexp = "[A-z\\d][A-z\\d.]{3,199}",
            message = "Логин должен содержать латинские буквы, цифры или символ \".\". Длина логины должна иметь хотя-бы три символ. Логин не может начинаться с \".\"")
    @Column(name = "login", updatable = false)
    private String login;

    /**
     * Пароль.
     */
    /*@Pattern(regexp = "[A-z\\d!#$*]{8,200}",
            message = "password может содержать латинские буквы, цифры и символы !#$*. Длина должна быть от 8 до 200 символов")*/
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
    @NotBlank(message = "email не должен быть пустым и иметь хотя бы один не пробельный символ")
    @Column(name = "email")
    private String email;

    /**
     * Роль.
     */

    @JoinColumn(name = "role_name")
    @ManyToOne
    private Role role;

    /**
     * Персональная скидка, [%].
     */
    @Range(min = 0, max = 100, message = "discount должен быть в диапазоне от 0 до 100")
    @Column(name = "discount", insertable = false)
    private int discount;

    /**
     * Аккаунт удален.
     */
    @Column(name = "is_delete", insertable = false)
    private boolean deleted;

    /**
     * Список записей.
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Recording> recordings = new ArrayList<>();
}
