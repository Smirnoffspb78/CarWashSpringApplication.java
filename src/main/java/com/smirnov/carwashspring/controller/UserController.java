package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.dto.response.get.RecordingReservedDTO;
import com.smirnov.carwashspring.dto.response.get.RecordingCompletedDTO;
import com.smirnov.carwashspring.service.RecordingService;
import com.smirnov.carwashspring.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @GetMapping("/{id}/records-reserved/{authUserId}")
    @Secured({"ROLE_ADMIN", "ROLE_OPERATOR","ROLE_USER"})
    @PreAuthorize("authentication.principal.id == #authUserId")
    public List<RecordingReservedDTO> getAllActiveReserveByUserId(@PathVariable("id") Integer userId, @PathVariable("authUserId") Integer authUserId) {
        log.info("GET: /users/{}/records-reserved", userId);
        return recordingService.getAllActiveReserveByUserId(userId, authUserId);
    }

    /**
     * Возвращает все выполненные записи по идентификатору пользователя.
     * Права доступа: ADMIN, OPERATOR с данным id, USER с данным id
     * @param userId Идентификатор пользователя
     * @return Список выполненных записей
     */
    @GetMapping("/{id}/records-completed/{authUserId}")
    @Secured({"ROLE_ADMIN", "ROLE_OPERATOR","ROLE_USER"})
    @PreAuthorize("authentication.principal.id == #authUserId")
    public List<RecordingCompletedDTO> getAllCompletedByUserId(@PathVariable("id") Integer userId, @PathVariable("authUserId") Integer authUserId) {
        log.info("GET: /users/{userId}/records-completed");
        return recordingService.getAllCompletedRecordingByUserId(userId, authUserId);
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
     * Удаляет пользователя из базы.
     * Права доступа: USER с удаляемым id.
     * @param id Идентификатор пользователя
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured("ROLE_USER")
    @PreAuthorize("authentication.principal.id == #userId")
    public void deleteUser(@PathVariable(name = "id") Integer  id, @RequestParam(name = "userId") Integer  userId) {
        log.info("PUT: /users/{}?userId={}", id, userId);
        userService.deleteUser(id, userId);
    }
}
