package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.dto.RangeForProfitDTO;
import com.smirnov.carwashspring.service.RecordService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * Контроллер для записи.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/record")
public class RecordController {

    /**
     * Сервисынй слой записи.
     */
    RecordService recordService;

    /**
     * Возвращает выручку за заданный промежуток времени.
     * Права доступа: Admin.
     * @param rangeForProfit DTO объект для передаче диапиазона периода.
     * @return Выручка за заданный промежуток времени
     */
    @GetMapping("/profit")
    public BigDecimal getProfit(@RequestBody RangeForProfitDTO rangeForProfit) {
        if (rangeForProfit == null) {
            throw new NullPointerException("rangeForProfit is null");
        }
        return recordService.getProfit(rangeForProfit.getStart(), rangeForProfit.getFinish());
    }

    /**
     * Отмечает запись, как выполненую, если она не выполнена, по ее идентификатору.
     * Права доступа: ADMIN, OPERATOR.
     * @param id Идентификатор записи
     */
    @PutMapping("/complite/{id}")
    public void updateRecordComplite(@PathVariable(name = "id") Integer id) {
        recordService.updateCompliteById(id);
    }
}
