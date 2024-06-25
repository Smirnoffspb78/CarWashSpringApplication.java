package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.dto.response.get.RecordingDTO;
import com.smirnov.carwashspring.dto.request.create.BoxCreateDTO;
import com.smirnov.carwashspring.dto.range.RangeDataTimeDTO;
import com.smirnov.carwashspring.service.BoxService;
import com.smirnov.carwashspring.service.RecordingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Контроллер для бокса.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/boxes")
public class BoxController {

    /**
     * Сервисный слой бокса.
     */
    private final BoxService boxService;

    private final RecordingService recordingService;

    /**
     * Регистрирует новый Бокс.
     * Права доступа - ADMIN.
     *
     * @param boxCreateDto DTO Бокс
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Integer addBox(@RequestBody @Valid BoxCreateDTO boxCreateDto) {
        return boxService.save(boxCreateDto);
    }

    /**
     * Возвращает список записей бокса по идентификатору.
     * Права доступа: ADMIN, OPERATOR(если работает в этом боксе).
     *
     * @param id идентификатор бокса
     * @return список записей бокса
     */
    @GetMapping("/{id}/recordings")
    public List<RecordingDTO> getRecordingsById(@PathVariable("id") Integer id) {
        return boxService.getAllRecordingById(id);
    }

    /**
     * Возвращает список записей бокса за диапазон даты, времени по идентификатору бокса.
     * Права доступа: ADMIN, OPERATOR(если работает в этом боксе).
     *
     * @param rangeDataTimeDTO Диапазон даты, времени
     * @param boxId            - Идентификатор бокса
     * @return Список записей за диапазон даты, времени.
     */
    @GetMapping("{id}/by-range-date-time/")
    public List<RecordingDTO> getAllRecordingsByDateTimeRangeAndBoxId(@RequestBody @Valid RangeDataTimeDTO rangeDataTimeDTO,
                                                                      @PathVariable("id") Integer boxId) {
        return recordingService.getAllRecordingsByRangeAndIdBox(rangeDataTimeDTO, boxId);
    }


}
