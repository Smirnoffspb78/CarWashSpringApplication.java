package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.entity.service.CarWashService;
import com.smirnov.carwashspring.repository.CarWashServiceRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервисный слой услуг.
 */
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class WorkService {

    /**
     * Репозиторий услуг.
     */
    private final CarWashServiceRepository carWashServiceRepository;

    /**
     * Вовзращет весь список услуг.
     *
     * @return Спислк услуг
     */

    public List<CarWashService> getAllServices() {
        log.info("getAllServices");
        return carWashServiceRepository.findAll();
    }

    /**
     * Сохраняет новую услугу в БД
     *
     * @param service Услуга
     */
    public void save(@Valid CarWashService service) {
        carWashServiceRepository.save(service);
    }
}
