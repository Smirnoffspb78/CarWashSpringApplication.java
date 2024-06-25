package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.dto.response.get.CarWashServiceDTO;
import com.smirnov.carwashspring.service.CarWashServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/services")
@Slf4j
public class CarWashServiceController {


    /**
     * Сервисный слой услуг.
     */
    private final CarWashServiceService carWashServiceService;


    /**
     * Возвращает список всех услуг из БД в формате JSON.
     * Права доступа: ADMIN, OPERATOR, USER.
     *
     * @return Список услуг
     */
    @GetMapping
    public List<CarWashServiceDTO> getAllService() {
        log.info("GET /services");
        return carWashServiceService.getAllServices();
    }

    /**
     * Регистрирует новую услугу.
     * Права доступа - ADMIN.
     *
     * @param serviceDTO Услуга
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Integer createService(@RequestBody @Valid CarWashServiceDTO serviceDTO) {
        return carWashServiceService.save(serviceDTO);
    }
}