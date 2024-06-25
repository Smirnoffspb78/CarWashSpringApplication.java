package com.smirnov.carwashspring.service;


import com.smirnov.carwashspring.dto.request.create.UserCreateDTO;
import com.smirnov.carwashspring.entity.service.Recording;
import com.smirnov.carwashspring.entity.users.DiscountWorker;
import com.smirnov.carwashspring.entity.users.Role;
import com.smirnov.carwashspring.entity.users.RolesUser;
import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.exception.LoginNotFoundException;
import com.smirnov.carwashspring.exception.UserNotFoundException;
import com.smirnov.carwashspring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

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
    private final DiscountWorkerService discountWorkerService;

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
        User user = getUserByIdAndRole(id, role);
        role.setRolesUser(RolesUser.OPERATOR);
        DiscountWorker discountWorker = new DiscountWorker();
        discountWorker.setUser(user);
        user.setRole(role);
        discountWorkerService.saveDiscountWorker(discountWorker);
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
                                      int discount,
                                      String typeDiscount) {
        DiscountWorker discountWorker = discountWorkerService.getDiscountWorkerById(id);
        TypeDiscount typeDiscountEnum = TypeDiscount.valueOf(typeDiscount.toUpperCase());
        switch (typeDiscountEnum) {
            case MIN -> {
                if (discount > discountWorker.getMaxDiscountForUsers()) {
                    throw new IllegalArgumentException("minDiscountForUsers не должен быть больше, чем max");
                }
                discountWorker.setMinDiscountForUsers(discount);
            }
            case MAX -> {
                if (discount < discountWorker.getMinDiscountForUsers()) {
                    throw new IllegalArgumentException("minDiscountForUsers не должен быть больше, чем max");
                }
                discountWorker.setMaxDiscountForUsers(discount);
            }
            default -> throw new IllegalStateException("Unexpected value: " + typeDiscountEnum);
        }
    }

    /**
     * Добавляет в систему нового пользователя.
     *
     * @param userCreateDTO Пользователь
     */
    @Transactional
    public Integer createUser(UserCreateDTO userCreateDTO) {
        User user = modelMapper.map(userCreateDTO, User.class);
        User userSave = userRepository.findByLoginAndDeletedIsFalse(user.getLogin()).orElse(null);
        if (userSave != null) {
            throw new LoginNotFoundException("login уже занят");
        }
        return userRepository.save(user).getId();
    }

    /**
     * Удаляет пользователя с ролью USER по идентификатору.
     *
     * @param id Идентификатор USER
     */
    @Transactional
    public void deleteUser(Integer id) {
        User user = getUserById(id);
        user.getRecordings().stream()
                .filter(Recording::isReserved)
                .forEach(recording -> recording.setReserved(false));
        user.setDeleted(true);
    }

    /**
     * Назначает скидку пользователю по идентификатору
     *
     * @param discount          Размер скидки, [%]
     * @param idOperator Идентификатор оператора или админа
     * @param idUser            Идентификатор пользователя
     */
    @Transactional
    public void activateDiscount(int discount, Integer idOperator, Integer idUser) {
        User user = getUserById(idUser);
        DiscountWorker discountWorker = discountWorkerService.getDiscountWorkerById(discount);
        if (discount > discountWorker.getMaxDiscountForUsers() || discount < discountWorker.getMinDiscountForUsers()) {
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
        User user = getUserById(id);
        user.setDiscount(0);
    }
    
    public User getUserById(Integer id) {
        return userRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id %d не найден".formatted(id)));
    }
    public User getUserByIdAndRole(Integer id, Role role) {
        return userRepository.findByIdAndRoleAndDeletedIsFalse(id, role).
                orElseThrow(() -> new IllegalArgumentException("Оператор с таким id не найден или его права не OPERATOR"));
    }

    public void checkUserById (Integer id){
        if (!userRepository.existsById(id)){
            throw new UserNotFoundException("Пользователь с таким id не найден");
        }
    }
}
