package com.smirnov.carwashspring.repository;

import com.smirnov.carwashspring.entity.users.Role;
import com.smirnov.carwashspring.entity.users.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий пользователя.
 */
@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    /**
     * Возвращает пользователя по id и Прав доступа.
     * @param id Идентификатор
     * @param role Права доступа
     * @return Пользователь
     */
    Optional<User> findByIdAndRole(Integer id, Role role);

    @Query(nativeQuery = true, value = "SELECT * FROM users WHERE id=:id AND (role_name='ADMIN' OR role_name='OPERATOR') ")
    //@Query(value = "SELECT u FROM User u WHERE u.id=?1 AND (u.role = 'ADMIN' OR u.role = 'OPERATOR')")
    Optional<User> findByIdAndRoleIsOperatorOrRoleIsAdmin(Integer id);
    /**
     * Возвращает пользователя по логину, если он не удален.
     * @param login Логин
     * @return Пользователь
     */
    Optional<User> findByLoginAndDeletedIsFalse(String login);

    /**
     * Возвращает пользователя по идентификатору, если он не удален.
     * @param id Логин
     * @return Пользователь
     */
    Optional<User> findByIdAndDeletedIsFalse(Integer id);

    /**
     * Проверяет, есть ли пользователь с идентификатором и правами доступу.
     * @param id Идентификатор
     * @param role Права доступа
     * @return true/false, если пользователь нашелся/не нашелся
     */

    boolean existsByIdAndRole(Integer id, Role role);

}
