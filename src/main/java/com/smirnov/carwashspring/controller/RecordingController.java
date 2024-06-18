package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.dto.RangeTimeDTO;
import com.smirnov.carwashspring.dto.RecordingDTO;
import com.smirnov.carwashspring.service.RecordingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Контроллер для записи.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/recordings")
@Validated
public class RecordingController {

    /**
     * Сервисынй слой записи.
     */
    private final RecordingService recordingService;

    /**
     * Возвращает выручку за заданный промежуток времени.
     * Права доступа: ADMIN.
     *
     * @param rangeTimeDTO DTO объект для передаче диапиазона периода.
     * @return Выручка за заданный промежуток времени
     */
    @GetMapping("/profit")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal getProfit(@RequestBody @Valid RangeTimeDTO rangeTimeDTO) {
        checkRangeRecording(rangeTimeDTO);
        return recordingService.getProfit(rangeTimeDTO.start(), rangeTimeDTO.finish());
    }

    /**
     * Отмечает запись, как выполненую, если она не выполнена, по ее идентификатору.
     * Права доступа: ADMIN, OPERATOR.
     *
     * @param id Идентификатор записи
     */
    @PutMapping("/complite/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateRecordComplite(@PathVariable(name = "id") @NotNull(message = "id is null") Integer id) {
        recordingService.updateCompliteById(id);
    }

    /**
     * Возвращает список записей бокса по идентефикатору.
     * Права доступа: ADMIN, OPERATOR.
     *
     * @param idBox идентификатор бокса
     * @return список записей бокса
     */
    @GetMapping("/by-box/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<RecordingDTO> getAllRecordingsByIdBox(@PathVariable("id") @NotNull(message = "idBox is null") Integer idBox) {
        return recordingService.getAllRecordingsByIdBox(idBox);
    }

    /**
     * Возвращает список записей бокса за диапазон даты, времени..
     * Права доступа: ADMIN, OPERATOR.
     *
     * @param rangeTimeDTO диапазон даты, времени
     * @return список записей за диапазон даты, времени.
     */
    @GetMapping("/by-range-date-time")
    public List<RecordingDTO> getAllRecordingsByDateTimeRange(@RequestBody @Valid RangeTimeDTO rangeTimeDTO) {
        checkRangeRecording(rangeTimeDTO);
        return recordingService.getAllRecordingsByRange(rangeTimeDTO);
    }

    /**
     * Возвращает список записей бокса за диапазон даты, времени по идентификатору бокса.
     * Права доступа: ADMIN, OPERATOR.
     *
     * @param rangeTimeDTO Диапазон даты, времени
     * @param boxId        - Идентификатор бокса
     * @return Список записей за диапазон даты, времени.
     */
    @GetMapping("/by-range-date-time/{boxId}")
    public List<RecordingDTO> getAllRecordingsByDateTimeRangeAndBoxId(@RequestBody @Valid RangeTimeDTO rangeTimeDTO,
                                                                      @PathVariable("boxId") @NotNull(message = "boxId is null") Integer boxId) {
        checkRangeRecording(rangeTimeDTO);
        return recordingService.getAllRecordingsByRangeAndIdBox(rangeTimeDTO, boxId);
    }

    /**
     * Вспомогательный метод проверяет корректность введенных даты и времени.
     *
     * @param rangeTimeDTO диапазон даты и времени
     */
    private void checkRangeRecording(RangeTimeDTO rangeTimeDTO) {
        if (rangeTimeDTO.start().isAfter(rangeTimeDTO.finish())) {
            throw new IllegalArgumentException("startDate cannot be after finishDate");
        }
    }
}
