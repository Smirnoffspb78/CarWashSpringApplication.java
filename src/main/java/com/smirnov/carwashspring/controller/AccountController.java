package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для акканута.
 */
@AllArgsConstructor
@RestController()
@RequestMapping("/account")
public class AccountController {

    /**
     * Сервисный слой аккаунта.
     */
    private AccountService accountService;

    /**
     * Удаляет аккаунт из БД.
     * Уровень доступа: USER, имеющий данный id.
     * @param id Идентификатор
     */
    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable(name = "id") int id){
        accountService.deleteAccount(id);
    }
}
