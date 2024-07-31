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
import lombok.Getter;
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
     * Репозиторий пользователя.
     */
    private final UserRepository userRepository;
    /**
     * Сервисный слой записи.
     */
    private final RecordingService recordingService;

    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, @Lazy JwtAuthenticationFilter jwtAuthenticationFilter,
                       @Lazy RecordingService recordingService, ModelMapper modelMapper) {
        this.userRepository = userRepository;
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
        User user = userRepository.findByIdAndRoleAndDeletedIsFalse(id, new Role(RolesUser.ROLE_USER)).
                orElseThrow(() -> new EntityNotFoundException(User.class, id));
        Employee employee = modelMapper.map(user, Employee.class);
        Integer operatorId = userRepository.save(employee).getId();
        employee.setId(operatorId);
        recordingService.getAllRecordingsUser(user).forEach(r -> r.setUser(employee));
        user.setDeleted(true);
        log.info("{}. user c id {} назначена роль OPERATOR. Новый id user: {}", HttpStatus.NO_CONTENT, id, operatorId);
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
    public void deleteUser(Integer id, Integer userId) {
        if (!id.equals(userId)) {
            throw new ForbiddenAccessException(id);
        }
        User user = getUserById(id);
        user.getRecordings().stream()
                .filter(Recording::isReserved)
                .forEach(recording -> recording.setReserved(false));
        user.setDeleted(true);
    }


    /**
     * Возвращает пользователя по его идентификатору
     * @param id Идентификатор пользователя
     * @return Пользователь
     */
    public User getUserById(Integer id) {
        return userRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, id));
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
