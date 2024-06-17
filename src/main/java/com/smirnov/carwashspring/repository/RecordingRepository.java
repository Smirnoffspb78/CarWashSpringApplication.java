package com.smirnov.carwashspring.repository;

import com.smirnov.carwashspring.entity.Recording;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Репозиторий записи.
 */
@Repository
public interface RecordingRepository extends CrudRepository<Recording, Integer> {

    /**
     * Подсчитывает выручку за заданный период времени.
     * @param start начало периода
     * @param finish Дата окончания периода
     * @return Выручка
     */
    @Query(nativeQuery = true, value = Queries.FIND_PROFIT_BY_RANGE)
    Optional<BigDecimal> findSumByRange(LocalDateTime start, LocalDateTime finish);

    /**
     * Возвращает запись по идентификатору, если запись не выполнена.
     * @param id Идентификатор
     * @return Запись
     */

    Optional<Recording> findByIdAndComplitedIsFalse(Integer id);


}
