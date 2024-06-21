package com.smirnov.carwashspring.repository;

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

    Optional<Recording> findByIdAndComplitedIsFalseAndReservedIsTrue(Integer id);

    /**
     * Возвращает запись поее идентификатору, если она забронирована.
     * @param id идентификатор записи
     * @return Запись, если она забронирована
     */
    Optional<Recording> findByIdAndReservedIsTrue(Integer id);
    /**
     * Возвращает все записи из Бокса
     *
     * @param id Идентификатор бокса
     * @return записи из бокса
     */
    List<Recording> findAllByBox_Id(Integer id);

    /**
     * Вовзращает список записей за определенный диапазон даты, времени.
     * @param start Начало диапазона
     * @param finish Конец диапазона
     * @return Список записей
     */
    List<Recording> findAllByStartBetween(LocalDateTime start, LocalDateTime finish);

    /**
     * Вовзращает список записей за определенный диапазон даты, времени по идентификатору бокса.
     * @param id Идентификатор бокса
     * @param start Начало диапазона
     * @param finish Конец диапазона
     * @return Список записей
     */
    List<Recording> findAllByBox_IdAndStartBetween(Integer id, LocalDateTime start, LocalDateTime finish);


    /**
     * Вовзращает спсиок неотмененных невывыполненныхзаказов пользователя за заданный диапазон времени
     * @param userId Пользователь
     * @param start началод диапазона
     * @param finish конец диапазона
     * @return спсиок неотмененных невывыполненныхзаказов пользователя
     */
    @Query(nativeQuery = true, value = "SELECT * FROM records WHERE user_id = :userId AND ((start>=:start AND start<=:finish) OR (finish>=:start AND finish<=:finish))")
    List<Recording> findAllByUserAndStartAndFinishAndReservedIsTrueAndComplitedIsFalse(Integer userId, LocalDateTime start, LocalDateTime finish);
}
