package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.dto.UserCreateDTO;
import com.smirnov.carwashspring.entity.User;
import com.smirnov.carwashspring.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * Контроллер для пользователя.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/users")
@Validated
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
    public void updateDiscountForUser(@PathVariable(name = "id") @NotNull(message = "id is null") Integer id,
                                      @PathVariable(name = "discount") float discount,
                                      @PathVariable(name = "typeDiscount") String typeDiscount) {
        userService.updateDiscountForUser(id, discount, typeDiscount);
    }
    @PutMapping("/discount/{discount}/{idOperatorOrAdmin}/{idUser}")
    public void updateDiscountUser(@PathVariable(name = "discount") @Positive(message = "discount должен быть положительным") float discount,
                                   @PathVariable(name = "idUser") @NotNull(message = "id is null") Integer idUser,
                                   @PathVariable(name = "idOperatorOrAdmin") @NotNull(message = "id is null") Integer idOperatorOrAdmin) {
        userService.updateDiscount(discount, idOperatorOrAdmin, idUser);
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
    public void deleteUser(@PathVariable(name = "id") @NotNull(message = "id is null") Integer id) {
        userService.deleteUser(id);
    }
}
