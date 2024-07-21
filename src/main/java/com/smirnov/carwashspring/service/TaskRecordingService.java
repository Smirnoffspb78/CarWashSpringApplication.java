package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.repository.RecordingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Сервисный слой автоматически выполняемых задач при работе с записями.
 */
@Service
@EnableScheduling
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TaskRecordingService {

    private final RecordingRepository recordingRepository;
    /**
     * Время, через которое происходит проверка подтверждения записи.
     */
    private static final int TIME = 1;

    /**
     * Время, за которое запись должна подтвердиться, иначе будет аннулирована автоматически.
     */
    private static final int CHECK_TIME = 15;

    private static final int ARRIVAL_TIME = 10;

    @Scheduled(fixedRate = TIME, timeUnit = TimeUnit.MINUTES)
    public void checkRecords() {
        LocalDateTime presentTime = LocalDateTime.now();
        recordingRepository.findAllByBetween(presentTime.minusMinutes(CHECK_TIME), presentTime.plusMinutes(ARRIVAL_TIME))
                .forEach(r -> {
                    r.setRemoved(true);
                    log.info("Запись с id " + r.getId() + " удалена системой. Не пришло подтверждение в течении 15 минут.");
                });
    }
}
