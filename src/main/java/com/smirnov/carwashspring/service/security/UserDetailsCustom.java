package com.smirnov.carwashspring.service.security;

import com.smirnov.carwashspring.entity.users.RolesUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class UserDetailsCustom extends User {

    @EqualsAndHashCode.Include
    private final Integer id;
    private final RolesUser rolesUser;
    /**
     * Calls the more complex constructor with all boolean arguments set to {@code true}.
     *
     * @param username Логин пользователя
     * @param password Пароль пользователя
     * @param authorities Права доступа
     */
    public UserDetailsCustom(String username, String password, Collection<? extends GrantedAuthority> authorities, Integer id) {
        super(username, password, authorities);
        this.rolesUser = RolesUser.valueOf(authorities.stream()
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Список ролей не может быть null или пустым"))
                .getAuthority());
        this.id = id;
    }
}
