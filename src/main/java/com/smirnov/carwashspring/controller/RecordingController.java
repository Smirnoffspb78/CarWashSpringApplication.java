package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.annotation.UserExist;
import com.smirnov.carwashspring.dto.*;
import com.smirnov.carwashspring.service.RecordingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Контроллер для записи.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/recordings")
@Validated
public class RecordingController {

    /**
     * Сервисный слой записи.
     */
    private final RecordingService recordingService;

    /**
     * Возвращает выручку за заданный промежуток времени.
     * Права доступа: ADMIN.
     *
     * @param rangeDataTimeDTO DTO объект для передаче диапиазона периода.
     * @return Выручка за заданный промежуток времени
     */
    @GetMapping("/profit")
    public BigDecimal getProfit(@RequestBody @Valid RangeDataTimeDTO rangeDataTimeDTO) {
        return recordingService.getProfit(rangeDataTimeDTO);
    }

    /**
     * Отмечает запись, как выполненую, если она не выполнена, по ее идентификатору.
     * Права доступа: ADMIN, OPERATOR.
     *
     * @param id Идентификатор записи
     */
    @PutMapping("/complite/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateRecordComplite(@PathVariable(name = "id") Integer id) {
        recordingService.updateCompliteById(id);
    }

    /**
     * Снимает бронь с записи (делает ее отмененной) по идентификатору.
     * Права доступа: ADMIN и USER с id записи.
     *
     * @param id Идентификатор записи
     */
    @PutMapping("cancellation/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void cancellationRecordingById(@PathVariable("id") Integer id) {
        recordingService.cancellationReserveById(id);
    }

    /**
     * Возвращает список записей бокса по идентификатору.
     * Права доступа: ADMIN, OPERATOR.
     *
     * @param idBox идентификатор бокса
     * @return список записей бокса
     */
    @GetMapping("/by-box/{id}")
    public List<RecordingDTO> getAllRecordingsByIdBox(@PathVariable("id") Integer idBox) {
        return recordingService.getAllRecordingsByIdBox(idBox);
    }

    /**
     * Возвращает список записей бокса за диапазон даты, времени.
     * Права доступа: ADMIN, OPERATOR.
     *
     * @param rangeDataTimeDTO диапазон даты, времени
     * @return список записей за диапазон даты, времени.
     */
    @GetMapping("/by-range-date-time")
    public List<RecordingDTO> getAllRecordingsByDateTimeRange(@RequestBody @Valid RangeDataTimeDTO rangeDataTimeDTO) {
        return recordingService.getAllRecordingsByRange(rangeDataTimeDTO);
    }

    /**
     * Возвращает список записей бокса за диапазон даты, времени по идентификатору бокса.
     * Права доступа: ADMIN, OPERATOR.
     *
     * @param rangeDataTimeDTO Диапазон даты, времени
     * @param boxId            - Идентификатор бокса
     * @return Список записей за диапазон даты, времени.
     */
    @GetMapping("/by-range-date-time/{boxId}")
    public List<RecordingDTO> getAllRecordingsByDateTimeRangeAndBoxId(@RequestBody @Valid RangeDataTimeDTO rangeDataTimeDTO,
                                                                      @PathVariable("boxId") Integer boxId) {
        return recordingService.getAllRecordingsByRangeAndIdBox(rangeDataTimeDTO, boxId);
    }

    /**
     * Создает запись.
     * Права доступа: USER, ADMIN, OPERATOR
     *
     * @param recordingDTO DTO для создания записи
     * @return true/false, если запись создана/не создана
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public boolean createRecording(@RequestBody @Valid RecordingCreateDTO recordingDTO) {
        recordingService.createRecordingByIdUser(recordingDTO);
        return true;
    }

    /**
     * Возвращает все забронированные записи пользователя по его идентификатору.
     *
     * @param userId Идентификатор пользователя
     * @return Список забронированных записей
     */
    @GetMapping("/users/{id}/reserved")
    public List<RecordingReservedDTO> getAllActiveReserveByUserId(@PathVariable("id") @UserExist Integer userId) {
        return recordingService.getAllActiveReserveByIdUse(userId);
    }

    /**
     * Возвращает все выполненные записи по идентификатору пользователя.
     * @param userId Идентификатор пользователя
     * @return Список выполненных записей
     */
    @GetMapping("/users/{id}/complited")
    public List<RecordingComplitedDTO> getAllComplitedByUserId(@PathVariable("id")  @UserExist Integer userId) {
        return recordingService.getAllComplitedRecordingByUserId(userId);
    }
}
