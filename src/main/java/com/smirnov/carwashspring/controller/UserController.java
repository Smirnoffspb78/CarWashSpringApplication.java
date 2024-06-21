package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.dto.UserCreateDTO;
import com.smirnov.carwashspring.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * Контроллер для пользователя.
 */
@RequiredArgsConstructor
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
    public void updateUserBeforeOperator(@PathVariable(name = "id") Integer id) {
        userService.updateUserBeforeOperator(id);
    }

    /**
     * Изменяет скидку, предоставляему опреатором пользователю.
     * Права доступа: ADMIN.
     *
     * @param id           Идентификатор оператора
     * @param discount     Скидка
     * @param typeDiscount тип скидки: max или min, иначе выбрасывается исключение
     */
    @PutMapping("{id}/{discount-for-user}/{typeDiscount}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateDiscountForUser(@PathVariable(name = "id") Integer id,
                                      @PathVariable(name = "discount-for-user") int discount,
                                      @PathVariable(name = "typeDiscount") String typeDiscount) {
        userService.updateDiscountForUser(id, discount, typeDiscount);
    }

    /**
     * Назначает скидку пользователю.
     * Права доступа: ADMIN, OPERATOR.
     * @param discount скидка
     * @param idUser идентификатор пользователя
     * @param idOperatorOrAdmin Идентификатор Админа или Оператора.
     */
    @PutMapping("/discount/{discount}/{idOperatorOrAdmin}/{idUser}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void activateDiscountUser(@PathVariable(name = "discount") @Positive(message = "discount должен быть положительным") int discount,
                                     @PathVariable(name = "idUser") Integer idUser,
                                     @PathVariable(name = "idOperatorOrAdmin") Integer idOperatorOrAdmin) {
        userService.activateDiscount(discount, idOperatorOrAdmin, idUser);
    }

    /**
     * Удаляет скидку пользователя по его идентификатору.
     * Уровень доступа: ADMIN, OPERATOR
     * @param id Идентификатор пользователя
     */
    @PutMapping("deactivate-discount/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deactivateDiscountUser(@PathVariable(name = "id") Integer id) {
        userService.deactivateDiscount(id);
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
        userService.createUser(userCreateDTO);
    }

    /**
     * Удаляет пользователя из базы.
     * Уровень доступа: USER с удаляемым id.
     * @param id Идентификатор пользователя
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(name = "id") Integer id) {
        userService.deleteUser(id);
    }
}
