package com.smirnov.carwashspring.service;


import com.smirnov.carwashspring.entity.User;
import com.smirnov.carwashspring.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Сервисный слой пользователя.
 */
@AllArgsConstructor
@Service
@Validated
public class UserService {

    /**
     * Репозиторий пользователя.
     */
    private final UserRepository userRepository;

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
    public void updateDiscountForUser(int id,
                                      @Range(min = 0, max = 100, message = "discount должен находиться в диапазоне от 0 до 100 включительно") float discount,
                                      @NotNull String typeDiscount) {
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
    }

    /**
     * Добавляет в систему нового пользователя.
     *
     * @param user Пользователь
     */
    @Transactional
    public void createUser(@Validated User user) {
        User userSave = userRepository.findByLoginAndDeletedIsFalse(user.getLogin()).orElse(null);
        if (userSave != null) {
            throw new IllegalArgumentException("login уже занят");
        }
        userRepository.save(user);
    }

    /**
     * Удаляет юзера по идентификатору.
     *
     * @param id Идентификатор
     */
    @Transactional
    public void deleteUser(int id) {
        User user = userRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        user.setDeleted(true);
    }

    /**
     * Назначает скидку пользователю по идентификатору
     *
     * @param discount          Размер скидки, [%]
     * @param idOperatorOrAdmin Идентификатор оператора или админа
     * @param idUser            Идентификатор пользователя
     */
    @Transactional
    public void updateDiscount(float discount, Integer idOperatorOrAdmin, Integer idUser) {
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        User operatorOrAdmin = userRepository.findByIdAndRoleIsOperatorOrRoleIsAdmin(idOperatorOrAdmin).orElseThrow(() -> new IllegalArgumentException("operator or admin not found"));
        if (discount > operatorOrAdmin.getMaxDiscountForUsers() || discount < operatorOrAdmin.getMinDiscountForUsers()) {
            throw new IllegalArgumentException("discount должен быть в диапазоне от MinDiscount до MaxDiscount");
        }
        user.setDiscount(discount);
    }
}
