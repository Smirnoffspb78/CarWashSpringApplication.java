package com.smirnov.carwashspring.service;


import com.smirnov.carwashspring.entity.users.Account;
import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.repository.AccountRepository;
import com.smirnov.carwashspring.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервисный слой пользователя.
 */
@AllArgsConstructor
@Service
public class UserService {

    /**
     * Репозиторий пользователя.
     */
    private final UserRepository userRepository;
    /**
     * Репозиторий аккаунта
     */
    private final AccountRepository accountRepository;

    /**
     * Выдает права оператора пользователю.
     * Права доступа - ADMIN.
     *
     * @param id идентификатор пользователя
     */
    @Transactional
    public void updateUserBeforeOperator(int id) {
        User user = userRepository.findByIdAndRole(id, User.Role.USER)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким id не найден или его права не USER"));
        user.setRole(User.Role.OPERATOR);
        userRepository.save(user);
    }

    /**
     * Обновляет минимальную или максимальную скидку, предоставляемую оператором.
     * Уровень доступа: ADMIN.
     *
     * @param id           Идентификатор
     * @param discount     Скидка
     * @param typeDiscount Тип скидки, принимает значения min или max.
     */
    @Transactional
    public void updateDiscountForUser(int id, float discount, String typeDiscount) {
        if (discount < 0 || discount > 100) {
            throw new IllegalArgumentException("discount должен находиться в диапазоне от 0 до 100 включительно");
        }
        User user = userRepository.findByIdAndRole(id, User.Role.OPERATOR).
                orElseThrow(() -> new IllegalArgumentException("Оператор с таким id не найден или его права не OPERATOR"));
        switch (typeDiscount) {
            case "min" -> {
                if (discount > user.getMaxDiscountForUsers()) {
                    throw new IllegalArgumentException("minDiscountForUsers не должен быть больше, чем max");
                }
                user.setMinDiscountForUsers(discount);
            }
            case "max" -> {
                if (discount < user.getMinDiscountForUsers()) {
                    throw new IllegalArgumentException("minDiscountForUsers не должен быть больше, чем max");
                }
                user.setMaxDiscountForUsers(discount);
            }
            default -> throw new IllegalArgumentException("typeDiscount должен принимать значения min или max");
        }
        userRepository.save(user);
    }

    /**
     * Добавляет в систему нового пользователя.
     * @param account Аккаунт
     * @param user Пользователь
     */
    @Transactional
    public void createUser(Account account, User user) {
        userRepository.save(user);
        accountRepository.save(account);
    }
}
