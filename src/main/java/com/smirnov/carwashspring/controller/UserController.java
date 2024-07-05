package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.dto.response.get.RecordingReservedDTO;
import com.smirnov.carwashspring.dto.request.create.UserCreateDTO;
import com.smirnov.carwashspring.dto.response.get.RecordingComplitedDTO;
import com.smirnov.carwashspring.service.RecordingService;
import com.smirnov.carwashspring.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * Контроллер для пользователя.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Validated
@Slf4j
public class UserController {
    /**
     * Сервисный слой пользователя.
     */
    private final UserService userService;

    private final RecordingService recordingService;

    /**
     * Выдает пользователю права доступа оператора.
     * Права доступа: ADMIN.
     *
     * @param id Идентификатор
     */
    @PutMapping("{id}/user-before-operator")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Secured("ROLE_ADMIN")
    public void updateUserBeforeOperator(@PathVariable(name = "id") Integer id) {
        log.info("PUT: /users/{}/user-before-operator", id);
        userService.updateUserBeforeOperator(id);
    }

    /**
     * Изменяет скидку, предоставляемую оператором пользователю.
     * Права доступа: ADMIN.
     *
     * @param id           Идентификатор оператора
     * @param discount     Скидка
     * @param typeDiscount тип скидки: max или min, иначе выбрасывается исключение
     */
    @PutMapping("{id}/{discount-for-user}/{typeDiscount}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Secured("ROLE_ADMIN")
    public void updateDiscountForUser(@PathVariable(name = "id") Integer id,
                                      @PathVariable (name = "discount-for-user")
                                      @Range(min = 0, max = 100, message = "Скидка должна быть в диапазоне от 0 до 100")
                                      int discount,
                                      @PathVariable(name = "typeDiscount") @NotNull String typeDiscount) {
        log.info("PUT: /users/{}/{}/{}", id, discount, typeDiscount);
        userService.updateDiscountForUser(id, discount, typeDiscount);
    }

    /**
     * Назначает скидку пользователю.
     * Права доступа: ADMIN, OPERATOR.
     * @param discount скидка
     * @param idUser идентификатор пользователя
     * @param idOperatorOrAdmin Идентификатор Админа или Оператора.
     */
    @PutMapping("/{id}/{idOperatorOrAdmin}/{discount}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Secured({"ROLE_ADMIN", "ROLE_OPERATOR"})
    public void activateDiscountUser(@PathVariable(name = "discount") @Positive(message = "discount должен быть положительным") int discount,
                                     @PathVariable(name = "id") Integer idUser,
                                     @PathVariable(name = "idOperatorOrAdmin") Integer idOperatorOrAdmin) {
        log.info("PUT: /users/{}/{}/{}", idUser, idOperatorOrAdmin, discount);
        userService.activateDiscount(discount, idOperatorOrAdmin, idUser);
    }

    /**
     * Удаляет скидку пользователя по его идентификатору.
     * Уровень доступа: ADMIN, OPERATOR
     * @param id Идентификатор пользователя
     */
    @PutMapping("{id}/deactivate-discount")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Secured({"ROLE_ADMIN", "ROLE_OPERATOR"})
    public void deactivateDiscountUser(@PathVariable(name = "id") Integer id) {
        log.info("PUT /users/{}/deactivate-discount", id);
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
    public Integer addUser(@RequestBody @Valid UserCreateDTO userCreateDTO) {
        log.info("POST /users");
        return userService.createUser(userCreateDTO);
    }

    /**
     * Удаляет пользователя из базы.
     * Права доступа: USER с удаляемым id.
     * @param id Идентификатор пользователя
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured("ROLE_USER")
    public void deleteUser(@PathVariable(name = "id") Integer  id) {
        log.info("PUT: /users/{}", id);
        userService.deleteUser(id);
    }

    /**
     * Возвращает все забронированные записи пользователя по его идентификатору.
     * Права доступа: USER c данным id
     * @param userId Идентификатор пользователя
     * @return Список забронированных записей
     */
    @GetMapping("{id}/records-reserved")
    @Secured("ROLE_USER")
    public List<RecordingReservedDTO> getAllActiveReserveByUserId(@PathVariable("id") Integer userId) {
        log.info("GET: /users/{}/records-reserved", userId);
        return recordingService.getAllActiveReserveByIdUse(userId);
    }

    /**
     * Возвращает все выполненные записи по идентификатору пользователя.
     * Права доступа: USER с данным id
     * @param userId Идентификатор пользователя
     * @return Список выполненных записей
     */
    @GetMapping("/{id}/records-completed")
    @Secured("ROLE_USER")
    public List<RecordingComplitedDTO> getAllCompletedByUserId(@PathVariable("id") Integer userId) {
        log.info("GET /users/{userId}/records-completed");
        return recordingService.getAllComplitedRecordingByUserId(userId);
    }
}
