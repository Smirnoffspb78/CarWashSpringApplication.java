package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.entity.users.DiscountWorker;
import com.smirnov.carwashspring.exception.EntityNotFoundException;
import com.smirnov.carwashspring.repository.DiscountWorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service

@Transactional
@RequiredArgsConstructor
public class DiscountWorkerService {

    /**
     * Скидка оператора.
     */
    private final DiscountWorkerRepository discountWorkerRepository;

    /** Инициализирует скидку для нового оператора.
     *
     * @param discountWorker скидка
     */
    public void saveDiscountWorker(DiscountWorker discountWorker) {
        discountWorkerRepository.save(discountWorker);
    }

    /**
     * Возвращает скидку оператора по его Идентификатору.
     * @param id Идентификатор скидки
     * @return Скидка
     */
    public DiscountWorker getDiscountWorkerById(Integer id) {
        return discountWorkerRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(DiscountWorker.class, id));
    }
}
