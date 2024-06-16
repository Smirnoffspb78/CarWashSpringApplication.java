package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.entity.service.Box;
import com.smirnov.carwashspring.service.BoxService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Контроллер для бокса.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/box")
public class BoxController {

    /**
     * Сервисный слой бокса.
     */
    private final BoxService boxService;

    /**
     * Регестрирует новый Бокс.
     * Права доступа - ADMIN.
     *
     * @param box Бокс
     */
    @PostMapping
    public void addBox(@RequestBody Box box) {
        if (box == null) {
            throw new NullPointerException("box is null");
        }
        boxService.save(box);
    }
}
