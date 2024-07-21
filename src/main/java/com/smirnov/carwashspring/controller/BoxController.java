package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.dto.response.get.RecordingDTO;
import com.smirnov.carwashspring.dto.request.create.BoxCreateDTO;
import com.smirnov.carwashspring.dto.range.RangeDataTimeDTO;
import com.smirnov.carwashspring.service.BoxService;
import com.smirnov.carwashspring.service.RecordingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
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
 * Контроллер для бокса.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/boxes")
@Slf4j
public class BoxController {

    /**
     * Сервисный слой бокса.
     */
    private final BoxService boxService;

    private final RecordingService recordingService;

    /**
     * Возвращает список записей бокса по идентификатору.
     * Права доступа: ADMIN, OPERATOR, если работает в этом боксе.
     *
     * @param id идентификатор бокса
     * @return список записей бокса
     */
    @GetMapping("/{id}/recordings")
    @Secured({"ROLE_ADMIN", "ROLE_OPERATOR"})
    public List<RecordingDTO> getRecordingsById(@PathVariable("id") Integer id) {
        log.info("GET: /boxes/recordings/{}", id);
        return boxService.getAllRecordingById(id);
    }

    /**
     * Возвращает список записей бокса за диапазон даты, времени по идентификатору бокса.
     * Права доступа: ADMIN, OPERATOR(если работает в этом боксе).
     *
     * @param rangeDataTimeDTO Диапазон даты, времени
     * @param boxId            Идентификатор бокса
     * @return Список записей за диапазон даты, времени.
     */
    @GetMapping("{id}/by-range-date-time/")
    @Secured({"ROLE_ADMIN", "ROLE_OPERATOR"})
    public List<RecordingDTO> getAllRecordingsByDateTimeRangeAndBoxId(@RequestBody @Valid RangeDataTimeDTO rangeDataTimeDTO,
                                                                      @PathVariable("id") Integer boxId) {
        log.info("GET: /boxes/recordings/{}/by-range-date-time/", boxId);
        return recordingService.getAllRecordingsByRangeAndIdBox(rangeDataTimeDTO, boxId);
    }

    /**
     * Регистрирует новый Бокс.
     * Права доступа: ADMIN.
     *
     * @param boxCreateDto DTO Бокс
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("ROLE_ADMIN")
    public Integer addBox(@RequestBody @Valid BoxCreateDTO boxCreateDto) {
        log.info("POST: /boxes");
        return boxService.save(boxCreateDto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    public void updateBox(Integer id, @RequestBody @Valid BoxCreateDTO boxCreateDto){
        log.info("POST: /boxes/{}", id);
        boxService.updateBox(id, boxCreateDto);
    }
}
