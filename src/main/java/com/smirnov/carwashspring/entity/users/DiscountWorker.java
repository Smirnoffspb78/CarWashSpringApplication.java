package com.smirnov.carwashspring.entity.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;


/**
 * Скидка, предоставляемая работниками, [%].
 */
@Entity
@Table(name = "discount_workers")
@Getter
@Setter
public class DiscountWorker {

    @Id
    private int id;
    /**
     * Максимальная скидка, предоставляемая опреатором.
     */
    @Column(name = "min_discount_for_user", insertable = false)
    @Range(min = 0, max = 100, message = "minDiscountForUsers должен быть в диапазаоне от 0 до 100")
    private int minDiscountForUsers;

    /**
     * Максимальная скидка, предоставляемая опреатором.
     */
    @Column(name = "max_discount_for_user", insertable = false)
    @Range(min = 0, max = 100, message = "maxDiscountForUsers должен быть в диапазаоне от 0 до 100")
    private int maxDiscountForUsers;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;
}
