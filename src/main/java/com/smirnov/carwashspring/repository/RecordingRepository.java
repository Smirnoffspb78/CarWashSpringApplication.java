package com.smirnov.carwashspring.repository;

import com.smirnov.carwashspring.entity.service.Recording;
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
    @Query(value = "SELECT SUM(rs.cost) FROM Recording rs WHERE rs.start BETWEEN ?1 AND ?2 AND rs.complited = TRUE")
    Optional<BigDecimal> findSumByRange(LocalDateTime start, LocalDateTime finish);

    /**
     * Возвращает запись по идентификатору, если запись не выполнена.
     *
     * @param id Идентификатор записи
     * @return Запись, если она выполнена
     */
    Optional<Recording> findByIdAndComplitedIsFalse(Integer id);

    /**
     * Возвращает запись по ее идентификатору, если она забронирована.
     *
     * @param id Идентификатор записи
     * @return Запись, если она забронирована
     */
    Optional<Recording> findByIdAndReservedIsTrue(Integer id);

    /**
     * Возвращает все записи из Бокса
     *
     * @param id Идентификатор бокса
     * @return записи из бокса
     */
    List<Recording> findByBox_Id(Integer id);

    /**
     * Возвращает список записей за определенный диапазон даты, времени.
     *
     * @param start  Начало диапазона
     * @param finish Конец диапазона
     * @return Список записей
     */
    List<Recording> findByStartBetween(LocalDateTime start, LocalDateTime finish);

    /**
     * Возвращает список записей за определенный диапазон даты, времени по идентификатору бокса.
     *
     * @param id     Идентификатор бокса
     * @param start  Начало диапазона
     * @param finish Конец диапазона
     * @return Список записей
     */
    List<Recording> findByBox_IdAndStartBetween(Integer id, LocalDateTime start, LocalDateTime finish);


    /**
     * Возвращает список неотмененных заказов пользователя за заданный диапазон времени
     *
     * @param userId Пользователь
     * @param start  начало диапазона
     * @param finish конец диапазона
     * @return список неотмененных невыполненных заказов пользователя
     */
    @Query(value = "SELECT rs FROM Recording rs WHERE rs.user.id = ?1 AND rs.reserved = TRUE " +
            "AND ((rs.start BETWEEN ?2 AND ?3) OR (rs.finish BETWEEN ?2 AND ?3))")
    List<Recording> findByUserIdAndStartAndFinishAndReservedIsTrue(Integer userId, LocalDateTime start, LocalDateTime finish);

    /**
     * Возвращает список неотмененных записей по идентификатору бокса за заданный диапазон даты, времени.
     *
     * @param boxId  Идентификатор бокса
     * @param start  Начало диапазона
     * @param finish Окончание диапазона
     * @return Список записей
     */
    @Query(value = "SELECT rs FROM Recording rs WHERE rs.box.id = ?1 AND rs.reserved = TRUE AND " +
            "((rs.start BETWEEN ?2 AND ?3) OR (rs.finish BETWEEN ?2 AND ?3))")
    List<Recording> findByBox_IdAndReservedIsTrue(Integer boxId, LocalDateTime start, LocalDateTime finish);

    /**
     * Возвращает список активных броней по идентификатору пользователя.
     *
     * @param userId Идентификатор пользователя
     */
    List<Recording> findAllByUser_IdAndReservedIsTrue(Integer userId);

    /**
     * Возвращает список выполненных записей по идентификатору пользователя.
     *
     * @param userId Идентификатор пользователя
     */
    List<Recording> findAllByUser_idAndComplitedIsTrue(Integer userId);
}
