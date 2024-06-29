package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.dto.request.create.RecordingCreateDTO;
import com.smirnov.carwashspring.dto.response.get.RecordingDTO;
import com.smirnov.carwashspring.dto.range.RangeDataTimeDTO;
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
     * Возвращает выручку за заданный диапазон даты, времени.
     * Права доступа: ADMIN.
     *
     * @param rangeDataTimeDTO диапазона даты, времени.
     * @return Выручка за заданный диапазон даты, времени
     */
    @GetMapping("/profit")
    public BigDecimal getProfit(@RequestBody @Valid RangeDataTimeDTO rangeDataTimeDTO) {
        return recordingService.getProfit(rangeDataTimeDTO);
    }

    /**
     * Отмечает запись, как выполненную, если она не выполнена, по ее идентификатору.
     * Права доступа: ADMIN, OPERATOR(если работаем в этом боксе).
     *
     * @param id Идентификатор записи
     */
    @PutMapping("/{id}/complited")
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
    @PutMapping("{id}/cancellation")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void cancellationRecordingById(@PathVariable("id") Integer id) {
        recordingService.cancellationReserveById(id);
    }

    /**
     * Возвращает список записей за диапазон даты, времени.
     * Права доступа: ADMIN.
     *
     * @param rangeDataTimeDTO диапазон даты, времени
     * @return список записей за диапазон даты, времени.
     */
    @GetMapping("/by-range-date-time")
    public List<RecordingDTO> getAllRecordingsByDateTimeRange(@RequestBody @Valid RangeDataTimeDTO rangeDataTimeDTO) {
        return recordingService.getAllRecordingsByRange(rangeDataTimeDTO);
    }

    /**
     * Создает запись.
     * Права доступа: USER, ADMIN, OPERATOR
     *
     * @param recordingDTO DTO для создания записи
     * @return Идентификатор записи
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Integer createRecording(@RequestBody @Valid RecordingCreateDTO recordingDTO) {
        return recordingService.createRecordingByIdUser(recordingDTO);
    }

    @PostMapping("/{id}/approve")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void approveRecording(@PathVariable(name = "id") Integer id) {
        recordingService.approve(id);
    }

    /**
     * Обновляет запись по ее идентификатору.
     * Права доступа: ADMIN, OPERATOR, USER, чей id совпадает с id в записи (реализовано на уровне кода)
     * @param recordingDTO Параметры записи
     * @param id Идентификатор записи
     */
    @PutMapping("/{id}/update")
    public void updateRecording(@RequestBody @Valid RecordingCreateDTO recordingDTO,
                                   @PathVariable(name = "id") Integer id) {
        recordingService.updateRecording(id, recordingDTO);
    }
}
