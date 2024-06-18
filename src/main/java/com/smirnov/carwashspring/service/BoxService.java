package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.entity.Box;
import com.smirnov.carwashspring.entity.User;
import com.smirnov.carwashspring.repository.BoxRepository;
import com.smirnov.carwashspring.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервисный слой бокса.
 */
@Service
@AllArgsConstructor
public class BoxService {

    /**
     * Репозиторий бокса.
     */
    private final BoxRepository boxRepository;
    private final UserRepository userRepository;

    /**
     * Регестрирует новый бокс.
     *
     * @param box Бокс
     */
    @Transactional
    public void save(@Valid Box box) {
        if (box.getStart().isAfter(box.getFinish())) {
            throw new ValidationException("start не должен быть позднее finish");
        }
        if (!userRepository.existsByIdAndRole(box.getUser().getId(), User.Role.OPERATOR)){
            throw new IllegalArgumentException("Оператора с id %d не существует".formatted(box.getUser().getId()));
        }
        boxRepository.save(box);
    }


}
