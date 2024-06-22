package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.dto.BoxCreateDTO;
import com.smirnov.carwashspring.entity.service.Box;
import com.smirnov.carwashspring.entity.users.Role;
import com.smirnov.carwashspring.entity.users.RolesUser;
import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.repository.BoxRepository;
import com.smirnov.carwashspring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервисный слой бокса.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BoxService {

    /**
     * Репозиторий бокса.
     */
    private final BoxRepository boxRepository;
    /**
     * Репозиторий пользователя.
     */
    private final UserRepository userRepository;

    /**
     * Регестрирует новый бокс.
     *
     * @param boxCreateDto Создаваемый бокс
     */
    public void save(BoxCreateDTO boxCreateDto) {
        Role role = new Role();
        role.setRolesUser(RolesUser.ADMIN);
        if (!userRepository.existsByIdAndRole(boxCreateDto.userId(), role)) {
            throw new IllegalArgumentException("Оператора с id %d не существует".formatted(boxCreateDto.userId()));
        }
        Box box = new Box();
        box.setFinish(boxCreateDto.finish());
        box.setStart(boxCreateDto.start());
        box.setUsageRate(boxCreateDto.usageRate());
        User user = new User();
        user.setId(boxCreateDto.userId());
        box.setUser(user);
        boxRepository.save(box);
    }
}
