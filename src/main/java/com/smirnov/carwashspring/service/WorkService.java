package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.entity.Work;
import com.smirnov.carwashspring.repository.WorkRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class WorkService {

    private final WorkRepository workRepository;

    /**
     * Вовзращет весь список услуг.
     *
     * @return Спислк услуг
     */
    @Transactional
    public List<Work> getAllServices() {
        return workRepository.findAll();
    }

    /**
     * Сохраняет новую услугу в БД
     *
     * @param service Услуга
     */
    @Transactional
    public void save(@Valid Work service) {
        workRepository.save(service);
    }
}
