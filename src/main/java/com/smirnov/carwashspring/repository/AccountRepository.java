package com.smirnov.carwashspring.repository;

import com.smirnov.carwashspring.entity.users.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий аккаунта.
 */
@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    /**
     * Удаляет аккаунт по идентификатору
     * @param id Идентификатор
     */
    void deleteById(Integer id);
}
