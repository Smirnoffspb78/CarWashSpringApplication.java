package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.repository.AccountRepository;
import com.smirnov.carwashspring.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервисный слой аккаунта.
 */
@AllArgsConstructor
@Service
public class AccountService {

    /**
     * Репозиторий аккаунта.
     */
    private final AccountRepository accountRepository;
    /**
     * Репозиторий пользователя.
     */
    private final UserRepository userRepository;


    /**
     * Удаляет аккаунт по идентифактору.
     * @param id идентификатор
     */
    @Transactional
    public void deleteAccount(Integer id) {
        if (id == null){
            throw new NullPointerException("id is null");
        }
        accountRepository.deleteById(id);
        User user = userRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("user not found"));
        user.setDelete(true);
    }
}
