package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.dto.BoxCreateDTO;
import com.smirnov.carwashspring.entity.Box;
import com.smirnov.carwashspring.entity.User;
import com.smirnov.carwashspring.service.BoxService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
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
    @ResponseStatus(HttpStatus.CREATED)
    public void addBox(@RequestBody @Valid BoxCreateDTO boxCreateDto) {
        Box box =new Box();
        box.setFinish(boxCreateDto.finish());
        box.setStart(boxCreateDto.start());
        box.setUsageRate(boxCreateDto.usageRate());
        User user = new User();
        user.setId(boxCreateDto.userId());
        box.setUser(user);
        boxService.save(box);
    }
}
