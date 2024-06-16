package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.entity.service.Service;
import com.smirnov.carwashspring.service.ServiceService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер для услуг.
 */
@AllArgsConstructor
@RestController()
@RequestMapping("/service")
public class ServiceController {


    private final ServiceService serviceService;

    /**
     * Возврщает список всех услуг из БД в формате JSON.
     * Права доступа: ADMIN, OPERATOR, USER.
     *
     * @return Список услуг
     */
    @GetMapping
    public List<Service> getAllService() {
        return serviceService.getAllServices();
    }

    /**
     * Регистрирует новую услугу.
     * Права доступа - ADMIN.
     *
     * @param service Услуга
     */
    @PostMapping
    public void createService(@RequestBody Service service) {
        if (service == null) {
            throw new NullPointerException("service is null");
        }
        serviceService.save(service);
    }
}