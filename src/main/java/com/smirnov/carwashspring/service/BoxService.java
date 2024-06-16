package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.entity.service.Box;
import com.smirnov.carwashspring.repository.BoxRepository;
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

    /**
     * Регестрирует новый бокс.
     *
     * @param box Бокс
     */
    @Transactional
    public void save(@Valid Box box) {
        if (box.getStart().isAfter(box.getFinish())) {
            throw new ValidationException("start не должен позднее finish");
        }
        boxRepository.save(box);
    }
}
