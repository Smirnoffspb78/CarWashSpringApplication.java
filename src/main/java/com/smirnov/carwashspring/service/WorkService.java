package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.entity.Work;
import com.smirnov.carwashspring.repository.WorkRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class WorkService {

    private final WorkRepository workRepository;

    /**
     * Вовзращет весь список услуг.
     *
     * @return Спислк услуг
     */
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
