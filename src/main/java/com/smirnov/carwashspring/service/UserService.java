package com.smirnov.carwashspring.service;


import com.smirnov.carwashspring.dto.UserCreateDTO;
import com.smirnov.carwashspring.entity.service.Recording;
import com.smirnov.carwashspring.entity.users.Role;
import com.smirnov.carwashspring.entity.users.RolesUser;
import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.exception.LoginExistsException;
import com.smirnov.carwashspring.exception.UserNotFoundException;
import com.smirnov.carwashspring.repository.RecordingRepository;
import com.smirnov.carwashspring.repository.UserRepository;;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Сервисный слой пользователя.
 */
@RequiredArgsConstructor
@Service
@Validated
public class UserService {

    /**
     * Тип скидки
     */
    private enum TypeDiscount {
        MIN, MAX
    }

    /**
     * Репозиторий пользователя.
     */
    private final UserRepository userRepository;

    /**
     * Репозиторий записей.
     */
    private final RecordingRepository recordingRepository;

    private final ModelMapper modelMapper;

    /**
     * Выдает права оператора пользователю.
     * Права доступа - ADMIN.
     *
     * @param id идентификатор пользователя
     */
    @Transactional
    public void updateUserBeforeOperator(Integer id) {
        Role role = new Role();
        role.setRolesUser(RolesUser.USER);
        User user = userRepository.findByIdAndRole(id, role)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким id не найден или его права не USER"));
        role.setRolesUser(RolesUser.OPERATOR);
        user.setRole(role);
    }

    /**
     * Обновляет минимальную или максимальную скидку, предоставляемую оператором.
     * Уровень доступа: ADMIN.
     *
     * @param id           Идентификатор пользователя
     * @param discount     Скидка
     * @param typeDiscount Тип скидки, принимает значения min или max.
     */
    @Transactional
    public void updateDiscountForUser(Integer id,
                                      @Range(min = 0, max = 100, message = "discount должен находиться в диапазоне от 0 до 100 включительно") int discount,
                                      @NotNull String typeDiscount) {
        Role role = new Role();
        role.setRolesUser(RolesUser.OPERATOR);
        User user = userRepository.findByIdAndRole(id, role).
                orElseThrow(() -> new IllegalArgumentException("Оператор с таким id не найден или его права не OPERATOR"));
        TypeDiscount typeDiscountEnum = TypeDiscount.valueOf(typeDiscount.toUpperCase());
        switch (typeDiscountEnum) {
            case MIN -> {
                if (discount > user.getMaxDiscountForUsers()) {
                    throw new IllegalArgumentException("minDiscountForUsers не должен быть больше, чем max");
                }
                user.setMinDiscountForUsers(discount);
            }
            case MAX -> {
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
     * @param userCreateDTO Пользователь
     */
    @Transactional
    public void createUser(UserCreateDTO userCreateDTO) {
        User user = modelMapper.map(userCreateDTO, User.class);
        User userSave = userRepository.findByLoginAndDeletedIsFalse(user.getLogin()).orElse(null);
        if (userSave != null) {
            throw new LoginExistsException("login уже занят");
        }
        userRepository.save(user);
    }

    /**
     * Удаляет пользователя с ролью USER по идентификатору.
     *
     * @param id Идентификатор USER
     */
    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id %d не найден".formatted(id)));
        List<Recording> recordings = recordingRepository.findAllByUser_IdAndReservedIsTrue(id);
        recordings.forEach(recording -> recording.setReserved(false)); //todo настройка валидации
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
    public void activateDiscount(int discount, Integer idOperatorOrAdmin, Integer idUser) {
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id %d не найден".formatted(idUser)));
        User operatorOrAdmin = userRepository.findByIdAndRoleIsOperatorOrRoleIsAdmin(idOperatorOrAdmin)
                .orElseThrow(() -> new UserNotFoundException("Оператор и администратор с id %d не найден".formatted(idOperatorOrAdmin)));
        if (discount > operatorOrAdmin.getMaxDiscountForUsers() || discount < operatorOrAdmin.getMinDiscountForUsers()) {
            throw new IllegalArgumentException("discount должен быть в диапазоне от MinDiscount до MaxDiscount");
        }
        user.setDiscount(discount);
    }

    /**
     * Удаляет скидку пользователю по его идентификатору.
     *
     * @param id Идентификатор пользователя.
     */
    @Transactional
    public void deactivateDiscount(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id %d не найден".formatted(id)));
        user.setDiscount(0);
    }
}
