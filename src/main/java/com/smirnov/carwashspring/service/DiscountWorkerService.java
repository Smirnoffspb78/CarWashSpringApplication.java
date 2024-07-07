package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.entity.users.DiscountWorker;
import com.smirnov.carwashspring.exception.EntityNotFoundException;
import com.smirnov.carwashspring.repository.DiscountWorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
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
     * Возвращает скидку оператора по ее идентификатору.
     * @param id Идентификатор скидки
     * @return Скидка
     */
    public DiscountWorker getDiscountWorkerById(Integer id) {
        DiscountWorker discountWorker =  discountWorkerRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(DiscountWorker.class, id));
        log.info("Получена скидка с id {}", id);
        return discountWorker;
    }
}
