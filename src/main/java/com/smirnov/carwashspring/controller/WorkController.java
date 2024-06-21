package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.entity.Work;
import com.smirnov.carwashspring.service.WorkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер для услуг.
 */
@RequiredArgsConstructor
@RestController()
@RequestMapping("/works")
public class WorkController {


    /**
     * Сервисный слой услуг.
     */
    private final WorkService workService;

    /**
     * Возврщает список всех услуг из БД в формате JSON.
     * Права доступа: ADMIN, OPERATOR, USER.
     *
     * @return Список услуг
     */
    @GetMapping
    public List<Work> getAllService() {
        return workService.getAllServices();
    }

    /**
     * Регистрирует новую услугу.
     * Права доступа - ADMIN.
     *
     * @param service Услуга
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createService(@RequestBody @Valid Work service) {
        workService.save(service);
    }
}