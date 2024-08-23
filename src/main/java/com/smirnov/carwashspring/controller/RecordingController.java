package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.dto.request.create.RecordingCreateDTO;
import com.smirnov.carwashspring.dto.response.get.RecordingDTO;
import com.smirnov.carwashspring.dto.range.RangeDataTimeDTO;
import com.smirnov.carwashspring.service.RecordingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * Контроллер для записи.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/recordings")
@Slf4j
public class RecordingController {
    /**
     * Сервисный слой записи.
     */
    private final RecordingService recordingService;


    /**
     * Возвращает выручку за заданный диапазон даты, времени.
     * Права доступа: ADMIN.
     *
     * @param rangeDataTimeDTO диапазона даты, времени.
     * @return Выручка за заданный диапазон даты, времени
     */
    @GetMapping("/profit")
    @Secured("ROLE_ADMIN")
    public BigDecimal getProfit(@RequestBody @Valid RangeDataTimeDTO rangeDataTimeDTO) {
        log.info("GET: /profit");
        return recordingService.getProfit(rangeDataTimeDTO);
    }

    /**
     * Возвращает список записей за диапазон даты, времени.
     * Права доступа: ADMIN.
     *
     * @param rangeDataTimeDTO диапазон даты, времени
     * @return список записей за диапазон даты, времени.
     */
    @GetMapping("/by-range-date-time")
    @Secured("ROLE_ADMIN")
    public List<RecordingDTO> getAllRecordingsByDateTimeRange(@RequestBody @Valid RangeDataTimeDTO rangeDataTimeDTO) {
        log.info("GET: /recordings/by-range-date-time");
        return recordingService.getAllRecordingsByRange(rangeDataTimeDTO);
    }

    /**
     * Создает запись. Права доступа:
     * - ADMIN, чей id совпадает с idUser, указанным в записи;
     * - OPERATOR, чей id совпадает с idUser, указанным в записи;
     * - USER, чей id совпадает с idUser, указанным в записи.
     *
     * @param recordingDTO DTO для создания записи
     * @return Идентификатор записи
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({"ROLE_ADMIN", "ROLE_OPERATOR", "ROLE_USER"})
    @PreAuthorize("authentication.principal.id == #userId")
    public Integer createRecording(@RequestBody @Valid RecordingCreateDTO recordingDTO,
                                   @RequestParam(name = "userId") Integer userId) {
        log.info("POST: /recordings?userId={}", userId);
        return recordingService.createRecordingByIdUser(recordingDTO);
    }

    /**
     * Обновляет запись по ее идентификатору.
     * Права доступа: ADMIN, OPERATOR, USER, чей id совпадает с id в записи (реализовано на уровне кода)
     *
     * @param recordingDTO Параметры записи
     * @param id           Идентификатор записи
     */
    @PutMapping("/{id}/update")
    @Secured({"ROLE_ADMIN", "ROLE_OPERATOR", "ROLE_USER"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("authentication.principal.id == #userId")
    public void updateRecording(@RequestBody @Valid RecordingCreateDTO recordingDTO,
                                @PathVariable(name = "id") Integer id,
                                @RequestParam(name = "userId") Integer userId
    ) {
        log.info("PUT: /recordings/update?userId={}", userId);
        recordingService.updateRecording(id, recordingDTO, userId);
    }

    /**
     * Удаляет запись по ее идентификатору.
     * Права доступа:
     * - ADMIN;
     * - OPERATOR, если работает в этом боксе или является пользователем этой записи;
     * - USER, если является пользователем этой записи.
     *
     * @param id Идентификатор записи
     */
    @PutMapping("/{id}/cancellation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_ADMIN", "ROLE_OPERATOR, ROLE_USER"})
    @PreAuthorize("authentication.principal.id == #userId")
    public void cancellationRecording(@PathVariable("id") Integer id, @RequestParam("userId") Integer userId) {
        log.info("PUT: /recordings/{}/cancellation?userId={}", id, userId);
        recordingService.cancellationReserveById(id, userId);
    }

    /**
     * Подтверждает запись по ее идентификатору.
     * Права доступа:
     * - ADMIN, чей id совпадает с idUser, указанным в записи;
     * - OPERATOR, чей id совпадает с idUser, указанным в записи;
     * - USER, чей id совпадает с idUser, указанным в записи.
     *
     * @param id идентификатор записи
     */
    @PutMapping("/{id}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_ADMIN", "ROLE_OPERATOR", "ROLE_USER"})
    @PreAuthorize("authentication.principal.id == #userId")
    public void approveRecording(@PathVariable(name = "id") Integer id, @RequestParam(name = "userId") Integer userId) {
        log.info("POST: /recordings/{}/approve?userId={}", id, userId);
        recordingService.approve(id);
    }

    /**
     * Ставит в записи отметку о прибытии по ее идентификатору.
     * Права доступа:
     * - ADMIN, чей id совпадает с idUser, указанным в записи;
     * - OPERATOR, чей id совпадает с idUser, указанным в записи;
     * - USER, чей id совпадает с idUser, указанным в записи.
     *
     * @param id идентификатор записи
     */
    @PutMapping("/{id}/arrive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_ADMIN", "ROLE_OPERATOR", "ROLE_USER"})
    @PreAuthorize("authentication.principal.id == #userId")
    public void arriveRecording(@PathVariable(name = "id") Integer id,
                                @RequestParam(name = "userId") Integer userId) {
        log.info("POST: /recordings/{}/arrive?userId={}", id, userId);
        recordingService.arrive(id, userId);
    }

    /**
     * Отмечает запись, как выполненную, если она не выполнена, по ее идентификатору.
     * Права доступа:
     * - ADMIN;
     * - OPERATOR, если работаем в этом боксе.
     *
     * @param id Идентификатор записи
     */
    @PutMapping("/{id}/completed")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_ADMIN", "ROLE_OPERATOR"})
    @PreAuthorize("authentication.principal.id == #operatorId")
    public void updateRecordCompleted(@PathVariable(name = "id") Integer id, @RequestParam(name = "operatorId") Integer operatorId) {
        log.info("PUT: /recordings/{}/completed?operatorId={}", id, operatorId);
        recordingService.updateCompleteById(id, operatorId);
    }
}
