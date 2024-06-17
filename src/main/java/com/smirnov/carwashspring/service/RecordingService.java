package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.entity.Recording;
import com.smirnov.carwashspring.repository.RecordingRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Validated
public class RecordingService {

    /**
     * Репозиторий записи.
     */
    private final RecordingRepository recordingRepository;

    /**
     * Подсчитывает выручку за заданный промежуток времени.
     *
     * @param start  Дата начала периода
     * @param finish Дата окончания перода
     * @return Выручка за заданный промежуток времени
     */
    public BigDecimal getProfit(@NotNull(message = "start is null") LocalDateTime start,
                                @NotNull(message = "finish is null") LocalDateTime finish) {
        if (start.isAfter(finish)) {
            throw new IllegalArgumentException("start is after finish");
        }
        return recordingRepository.findSumByRange(start, finish).orElse(BigDecimal.valueOf(0.0));
    }

    @Transactional
    public void updateCompliteById(int id) {
        Recording recordingUpdate = recordingRepository.findByIdAndComplitedIsFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Записи с таким id остутсвует или она уже заврешенная"));
        recordingUpdate.setComplited(true);
    }
}
