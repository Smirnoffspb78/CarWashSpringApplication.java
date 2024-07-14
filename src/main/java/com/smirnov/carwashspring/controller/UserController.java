package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.dto.response.get.RecordingReservedDTO;
import com.smirnov.carwashspring.dto.response.get.RecordingCompletedDTO;
import com.smirnov.carwashspring.service.RecordingService;
import com.smirnov.carwashspring.service.UserService;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
     * Возвращает все забронированные записи пользователя по его идентификатору.
     * Права доступа: ADMIN, USER c данным id
     * @param userId Идентификатор пользователя
     * @return Список забронированных записей
     */
    @GetMapping("/{id}/records-reserved")
    @Secured({"ROLE_ADMIN","ROLE_USER"})
    public List<RecordingReservedDTO> getAllActiveReserveByUserId(@PathVariable("id") Integer userId) {
        log.info("GET: /users/{}/records-reserved", userId);
        return recordingService.getAllActiveReserveByUserId(userId);
    }

    /**
     * Возвращает все выполненные записи по идентификатору пользователя.
     * Права доступа: ADMIN, OPERATOR с данным id USER с данным id
     * @param userId Идентификатор пользователя
     * @return Список выполненных записей
     */
    @GetMapping("/{id}/records-completed")
    @Secured({"ROLE_ADMIN","ROLE_USER"})
    public List<RecordingCompletedDTO> getAllCompletedByUserId(@PathVariable("id") Integer userId) {
        log.info("GET: /users/{userId}/records-completed");
        return recordingService.getAllCompletedRecordingByUserId(userId);
    }

    /**
     * Выдает пользователю права доступа оператора.
     * Права доступа: ADMIN.
     *
     * @param id Идентификатор
     */
    @PutMapping("{id}/user-before-operator")
    @ResponseStatus(HttpStatus.NO_CONTENT)
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
    @PutMapping("{id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    public void updateDiscountForUser(@PathVariable(name = "id") Integer id,
                                      @RequestParam("discount-for-user")
                                      @Range(min = 0, max = 100, message = "Скидка должна быть в диапазоне от 0 до 100")
                                      int discount,
                                      @RequestParam(name = "typeDiscount")
                                          @Pattern(regexp = "max|min")
                                          String typeDiscount) {
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
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
    @PutMapping("/{id}/deactivate-discount")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_ADMIN", "ROLE_OPERATOR"})
    public void deactivateDiscountUser(@PathVariable(name = "id") Integer id) {
        log.info("PUT: /users/{}/deactivate-discount", id);
        userService.deactivateDiscount(id);
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
}
