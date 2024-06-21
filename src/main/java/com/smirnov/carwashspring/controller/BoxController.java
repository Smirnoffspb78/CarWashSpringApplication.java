package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.dto.BoxCreateDTO;
import com.smirnov.carwashspring.entity.Box;
import com.smirnov.carwashspring.entity.User;
import com.smirnov.carwashspring.service.BoxService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


/**
 * Контроллер для бокса.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/boxes")
@Validated
public class BoxController {

    /**
     * Сервисный слой бокса.
     */
    private final BoxService boxService;

    /**
     * Регестрирует новый Бокс.
     * Права доступа - ADMIN.
     *
     * @param boxCreateDto DTO Бокс
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) //todo перенсти в сервис
    public void addBox(@RequestBody @Valid BoxCreateDTO boxCreateDto) {
        if (boxCreateDto.start().isAfter(boxCreateDto.finish())) {
            throw new ValidationException("start не должен быть позднее finish");
        }
        boxService.save(boxCreateDto);
    }
}
