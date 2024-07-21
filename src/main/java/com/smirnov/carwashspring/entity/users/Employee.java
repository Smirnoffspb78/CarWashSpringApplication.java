package com.smirnov.carwashspring.entity.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;


/**
 * Работник.
 */
@Entity
@Table(name = "employees")
@Getter
@Setter
public class Employee extends User{

    /**
     * Максимальная скидка, предоставляемая оператором.
     */
    @Column(name = "min_discount_for_user")
    @Range(min = 0, max = 100, message = "minDiscountForUsers должен быть в диапазоне от 0 до 100")
    private int minDiscountForUsers;

    /**
     * Максимальная скидка, предоставляемая оператором.
     */
    @Column(name = "max_discount_for_user")
    @Range(min = 0, max = 100, message = "maxDiscountForUsers должен быть в диапазоне от 0 до 100")
    private int maxDiscountForUsers;
}
