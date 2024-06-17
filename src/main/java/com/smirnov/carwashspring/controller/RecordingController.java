package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.dto.RangeForProfitDTO;
import com.smirnov.carwashspring.service.RecordingService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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
     * Права доступа: Admin.
     * @param rangeForProfit DTO объект для передаче диапиазона периода.
     * @return Выручка за заданный промежуток времени
     */
    @GetMapping("/profit")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal getProfit(@RequestBody @Valid RangeForProfitDTO rangeForProfit) {
        return recordingService.getProfit(rangeForProfit.start(), rangeForProfit.finish());
    }

    /**
     * Отмечает запись, как выполненую, если она не выполнена, по ее идентификатору.
     * Права доступа: ADMIN, OPERATOR.
     * @param id Идентификатор записи
     */
    @PutMapping("/complite/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateRecordComplite(@PathVariable(name = "id") int id) {
        recordingService.updateCompliteById(id);
    }
}
