package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.dto.UserCreateDTO;
import com.smirnov.carwashspring.entity.User;
import com.smirnov.carwashspring.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


/**
 * Контроллер для пользователя.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/users")
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
    @ResponseStatus(HttpStatus.ACCEPTED)
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
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateDiscountForUser(@PathVariable(name = "id") int id,
                                      @PathVariable(name = "discount") float discount,
                                      @PathVariable(name = "typeDiscount") String typeDiscount) {
        userService.updateDiscountForUser(id, discount, typeDiscount);
    }

    /**
     * Добавляет нового пользователя в систему.
     * Права доступа: Анонимный пользователь.
     *
     * @param userCreateDTO параметры пользователя
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addUser(@RequestBody @Valid UserCreateDTO userCreateDTO) {
        if (userCreateDTO == null) {
            throw new NullPointerException("UserAndAccountDTO is null");
        }
        User user = new User();
        user.setEmail(userCreateDTO.email());
        user.setName(userCreateDTO.name());
        user.setLogin(userCreateDTO.login());
        user.setPassword(userCreateDTO.password());
        userService.createUser(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(name = "id") int id) {
        userService.deleteUser(id);
    }
}
