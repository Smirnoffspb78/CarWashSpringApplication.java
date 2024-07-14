package com.smirnov.carwashspring.service;


import com.smirnov.carwashspring.entity.service.Recording;
import com.smirnov.carwashspring.entity.users.Employee;
import com.smirnov.carwashspring.entity.users.Role;
import com.smirnov.carwashspring.entity.users.RolesUser;
import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.exception.EntityNotFoundException;
import com.smirnov.carwashspring.exception.ForbiddenAccessException;
import com.smirnov.carwashspring.exception.LoginException;
import com.smirnov.carwashspring.repository.UserRepository;
import com.smirnov.carwashspring.service.security.JwtAuthenticationFilter;
import com.smirnov.carwashspring.dto.response.get.UserDetailsCustom;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

/**
 * Сервисный слой пользователя.
 */

@Service
@Transactional
@Validated
@Slf4j
public class UserService implements UserDetailsService {

    /**
     * Тип скидки.
     */
    private enum TypeDiscount {
        MIN, MAX
    }

    /**
     * Репозиторий пользователя.
     */
    private final UserRepository userRepository;

    /**
     * Сервисный слой скидки, предоставляемой оператором.
     */
    private final EmployeeService employeeService;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final RecordingService recordingService;

    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, EmployeeService employeeService,
                       @Lazy JwtAuthenticationFilter jwtAuthenticationFilter, @Lazy RecordingService recordingService,
                       ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.employeeService = employeeService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.recordingService = recordingService;
        this.modelMapper = modelMapper;
    }

    /**
     * Выдает права оператора пользователю.
     * Права доступа - ADMIN.
     *
     * @param id идентификатор пользователя
     */

    public void updateUserBeforeOperator(Integer id) {
        User user = getUserByIdAndRole(id, new Role(RolesUser.ROLE_USER));
        Employee employee = modelMapper.map(user, Employee.class);
        Integer operatorId = employeeService.saveEmployee(employee);
        employee.setId(operatorId);
        recordingService.getAllRecordingsUser(user).forEach(r -> r.setUser(employee));
        user.setDeleted(true);
        log.info("user c id {} назначена роль OPERATOR. Новый id user: {}", id, operatorId);
    }

    /**
     * Обновляет минимальную или максимальную скидку, предоставляемую оператором.
     * Уровень доступа: ADMIN.
     *
     * @param id           Идентификатор пользователя
     * @param discount     Скидка
     * @param typeDiscount Тип скидки, принимает значения min или max.
     */
    public void updateDiscountForUser(Integer id,
                                      int discount,
                                      String typeDiscount) {
        Employee employee = employeeService.getEmployeeById(id);
        TypeDiscount typeDiscountEnum = TypeDiscount.valueOf(typeDiscount.toUpperCase());
        switch (typeDiscountEnum) {
            case MIN -> {
                if (discount > employee.getMaxDiscountForUsers()) {
                    log.error("minDiscountForUsers не должен быть больше, чем max. {}", HttpStatus.BAD_REQUEST);
                    throw new IllegalArgumentException("minDiscountForUsers не должен быть больше, чем max");
                }
                employee.setMinDiscountForUsers(discount);
            }
            case MAX -> {
                if (discount < employee.getMinDiscountForUsers()) {
                    log.error("minDiscountForUsers не должен быть больше, чем max. {}", HttpStatus.BAD_REQUEST);
                    throw new IllegalArgumentException("minDiscountForUsers не должен быть больше, чем max");
                }
                employee.setMaxDiscountForUsers(discount);
            }
        }
    }

    /**
     * Проверяет наличие Пользователя по логину.
     *
     * @param login Логин
     */
    public void checkUserByLogin(String login) {
        if (userRepository.existsByLoginAndDeletedIsFalse(login)) {
            throw new LoginException("login уже занят");
        }
    }

    /**
     * Сохраняет пользователя в базу данных
     *
     * @param user пользователь
     * @return Идентификатор пользователя
     */
    public Integer saveUser(User user) {
        return userRepository.save(user).getId();
    }

    /**
     * Удаляет пользователя с ролью USER по идентификатору.
     *
     * @param id Идентификатор USER
     */
    public void deleteUser(Integer id) {
        User user = getUserById(id);
        if (!id.equals(jwtAuthenticationFilter.getAuthUser().getId())) {
            throw new ForbiddenAccessException(id);
        }
        user.getRecordings().stream()
                .filter(Recording::isReserved)
                .forEach(recording -> recording.setReserved(false));
        user.setDeleted(true);
    }

    /**
     * Назначает скидку пользователю по идентификатору
     *
     * @param discount   Размер скидки, [%]
     * @param idOperator Идентификатор оператора или админа
     * @param idUser     Идентификатор пользователя
     */
    public void activateDiscount(int discount, Integer idOperator, Integer idUser) {
        User user = getUserById(idUser);
        Employee employee = employeeService.getEmployeeById(idOperator);
        if (discount > employee.getMaxDiscountForUsers() || discount < employee.getMinDiscountForUsers()) {
            throw new IllegalArgumentException("discount должен быть в диапазоне от MinDiscount до MaxDiscount");
        }
        user.setDiscount(discount);
    }

    /**
     * Удаляет скидку пользователю по его идентификатору.
     *
     * @param id Идентификатор пользователя.
     */
    public void deactivateDiscount(Integer id) {
        User user = getUserById(id);
        user.setDiscount(0);
        log.info("Скидка у пользователя с id {} удалена", id);
    }

    public User getUserById(Integer id) {
        return userRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, id));
    }

    public User getUserByIdAndRole(Integer id, Role role) {
        return userRepository.findByIdAndRoleAndDeletedIsFalse(id, role).
                orElseThrow(() -> new EntityNotFoundException(User.class, id));
    }

    public void checkUserById(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException(User.class, id);
        }
    }

    /**
     * Возвращает пользователя по логину.
     *
     * @param username логин пользователя
     * @return пользователь в контексте Spring Security
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetailsCustom loadUserByUsername(String username) {
        User user = userRepository.findByLoginAndDeletedIsFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
                user.getRole().getRolesUser().name()
        );
        log.info("Аутентифицирован user с login: {}. Роль: {}", user.getLogin(), grantedAuthority.getAuthority());
        return new UserDetailsCustom(username, user.getPassword(), Set.of(grantedAuthority), user.getId());
    }
}
