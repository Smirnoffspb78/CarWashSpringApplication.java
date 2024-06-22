package com.smirnov.carwashspring.entity.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Роли.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {

    /**
     * Идентификатор роли.
     */
    @Id
    @Enumerated(value = EnumType.STRING)
    @Column(name = "name")
    RolesUser rolesUser;

}
