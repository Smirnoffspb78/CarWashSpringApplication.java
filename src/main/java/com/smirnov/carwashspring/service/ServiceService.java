package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.repository.ServiceRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    /**
     * Вовзращет весь список услуг.
     *
     * @return Спислк услуг
     */
    public List<com.smirnov.carwashspring.entity.service.Service> getAllServices() {
        return serviceRepository.findAll();
    }

    /**
     * Сохраняет новую услугу в БД
     *
     * @param service Услуга
     */
    @Transactional
    public void save(@Valid com.smirnov.carwashspring.entity.service.Service service) {
        serviceRepository.save(service);
    }
}
