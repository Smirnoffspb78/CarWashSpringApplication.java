package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.entity.service.Record;
import com.smirnov.carwashspring.repository.RecordRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RecordService {

    /**
     * Репозиторий записи.
     */
    private final RecordRepository recordRepository;

    /**
     * Подсчитывает выручку за заданный промежуток времени.
     * @param start Дата начала периода
     * @param finish Дата окончания перода
     * @return Выручка за заданный промежуток времени
     */
    public BigDecimal getProfit(LocalDateTime start, LocalDateTime finish) {
        if (start == null || finish == null) {
            throw new NullPointerException("start or finish is null");
        }
        if (start.isAfter(finish)) {
            throw new IllegalArgumentException("start is after finish");
        }
        return recordRepository.findSumByRange(start, finish).orElse(BigDecimal.valueOf(0.0));
    }

    @Transactional
    public void updateCompliteById(Integer id) {
        Record recordUpdate = recordRepository.findByIdAndIsCompliteIsFalse(id)
                .orElseThrow(()->  new IllegalArgumentException("Записи с таким id остутсвует или она уже заврешенная"));
        recordUpdate.setComplite(true);
        recordRepository.save(recordUpdate);
    }
}
