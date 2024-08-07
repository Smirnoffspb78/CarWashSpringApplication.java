package com.smirnov.carwashspring.entity.users;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Роли.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Role {

    /**
     * Идентификатор роли.
     */
    @Id
    @Enumerated(value = EnumType.STRING)
    @Column(name = "name")
    private RolesUser rolesUser;

}
