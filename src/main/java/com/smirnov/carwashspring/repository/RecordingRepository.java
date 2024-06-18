package com.smirnov.carwashspring.repository;

import com.smirnov.carwashspring.entity.Box;
import com.smirnov.carwashspring.entity.Recording;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий записи.
 */
@Repository
public interface RecordingRepository extends CrudRepository<Recording, Integer> {

    /**
     * Подсчитывает выручку за заданный период времени.
     *
     * @param start  начало периода
     * @param finish Дата окончания периода
     * @return Выручка
     */
    @Query(nativeQuery = true, value = Queries.FIND_PROFIT_BY_RANGE)
    Optional<BigDecimal> findSumByRange(LocalDateTime start, LocalDateTime finish);

    /**
     * Возвращает запись по идентификатору, если запись не выполнена.
     *
     * @param id Идентификатор
     * @return Запись
     */

    Optional<Recording> findByIdAndComplitedIsFalse(Integer id);

    /**
     * Возвращает все записи из Бокса
     *
     * @param box Бокс
     * @return записи из бокса
     */
    List<Recording> findAllByBox(Box box);

    @Query(nativeQuery = true, value = "SELECT * FROM records WHERE start >= :start AND start <= :finish")
    List<Recording> findAllAndStartIsAfterAndStartIsBefore(LocalDateTime start, LocalDateTime finish);

    @Query(nativeQuery = true, value = "SELECT * FROM records WHERE start >= :start AND start <= :finish AND box_id = :id")
    List<Recording> findAllByBoxAndStartIsAfterAndStartIsBefore(LocalDateTime start, LocalDateTime finish, Integer id);
}
