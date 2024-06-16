package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.dto.UserAndAccountDTO;
import com.smirnov.carwashspring.entity.users.Account;
import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Контроллер для пользователя.
 */
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    /**
     * Сервисный слой пользователя.
     */
    private final UserService userService;

    /**
     * Выдает пользователю права доступа оператора.
     * Права доступа: Admin
     *
     * @param id Идентификатор
     */
    @PutMapping("/user-before-operator/{id}")
    public void updateUserBeforeOperator(@PathVariable(name = "id") int id) {
        userService.updateUserBeforeOperator(id);
    }

    /**
     * Изменяет скидку, предоставляему опреатором пользователю.
     * Права доступа: ADMIN.
     *
     * @param id           Идентификатор
     * @param discount     Скидка
     * @param typeDiscount тип скидки: max или min, иначе выбрасывается исключение
     */
    @PutMapping("/discount-for-user/{id}/{typeDiscount}/{discount}")
    public void updateDiscountForUser(@PathVariable(name = "id") int id, @PathVariable(name = "discount") float discount, @PathVariable(name = "typeDiscount") String typeDiscount) {
        userService.updateDiscountForUser(id, discount, typeDiscount);
    }

    /**
     * Добавляет нового пользователя в систему.
     * Права доступа: Анонимный пользователь.
     *
     * @param userAndAccountDTO параметры пользователя
     */
    @PostMapping
    public void addUser(@RequestBody UserAndAccountDTO userAndAccountDTO) {
        if (userAndAccountDTO == null) {
            throw new NullPointerException("UserAndAccountDTO is null");
        }
        Account account = new Account();
        account.setLogin(userAndAccountDTO.getLogin());
        account.setPassword(userAndAccountDTO.getPassword());
        User user = new User();
        user.setEmail(userAndAccountDTO.getEmail());
        user.setName(userAndAccountDTO.getName());
        account.setUser(user);
        userService.createUser(account, user);
    }
}
