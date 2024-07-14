package com.smirnov.carwashspring.repository;

import com.smirnov.carwashspring.entity.service.Box;
import com.smirnov.carwashspring.entity.service.Recording;
import com.smirnov.carwashspring.entity.users.User;
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
    @Query(value = "SELECT SUM(rs.cost) FROM Recording rs WHERE rs.start BETWEEN ?1 AND ?2 AND rs.completed = TRUE")
    Optional<BigDecimal> findSumByRange(LocalDateTime start, LocalDateTime finish);

    /**
     * Возвращает запись по идентификатору, если запись не выполнена.
     *
     * @param id Идентификатор записи
     * @return Запись, если она выполнена
     */
    Optional<Recording> findByIdAndCompletedIsFalseAndRemovedIsFalseAndReservedIsTrue(Integer id);

    /**
     * Возвращает запись по ее идентификатору, если она не отменена и не выполнена.
     *
     * @param id Идентификатор записи
     * @return Запись, если она забронирована
     */
    Optional<Recording> findByIdAndRemovedIsFalseAndCompletedIsFalse(Integer id);

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
     * @param box    Бокс
     * @param start  Начало диапазона
     * @param finish Конец диапазона
     * @return Список записей
     */
    List<Recording> findByBoxAndStartBetween(Box box, LocalDateTime start, LocalDateTime finish);


    /**
     * Возвращает список неудаленных невыполненных заказов пользователя за заданный диапазон времени.
     *
     * @param userId Пользователь
     * @param start  Начало диапазона
     * @param finish Конец диапазона
     * @return Список неотмененных невыполненных заказов пользователя
     */
    @Query(value = """
            SELECT rs FROM Recording rs
            WHERE rs.user.id = ?1
            AND rs.removed = FALSE
            AND rs.completed = FALSE
            AND ((rs.start >= ?2 AND rs.start < ?3)
            OR (rs.finish > ?2 AND rs.finish <= ?3)
            OR (rs.start <= ?2 AND rs.finish >= ?3))""")
    List<Recording> findByUserIdAndStartAndFinishAndRemovedIsFalse(Integer userId, LocalDateTime start, LocalDateTime finish);

    /**
     * Возвращает список неудаленных невыполненных заказов пользователя за заданный диапазон времени,
     * кроме заказа, который редактируется.
     *
     * @param userId Пользователь
     * @param start  начало диапазона
     * @param finish конец диапазона
     * @return список неотмененных невыполненных заказов пользователя
     */
    @Query(value = """
            SELECT rs FROM Recording rs
            WHERE rs.user.id = ?1
            AND rs.removed = FALSE
            AND rs.completed = FALSE
            AND rs.id != ?4
            AND ((rs.start >= ?2 AND rs.start < ?3)
            OR (rs.finish > ?2 AND rs.finish <= ?3)
            OR (rs.start <= ?2 AND rs.finish >= ?3))""")
    List<Recording> findByIdAndUserIdAndStartAndFinishAndRemovedIsFalse(Integer userId, LocalDateTime start, LocalDateTime finish, Integer id);

    /**
     * Возвращает список неотмененных невыполненных записей по идентификатору бокса за заданный диапазон даты, времени.
     *
     * @param boxId  Идентификатор бокса
     * @param start  Начало диапазона
     * @param finish Окончание диапазона
     * @return Список записей
     */
    @Query(value = """
            SELECT rs FROM Recording rs
            WHERE rs.box.id = ?1
            AND rs.removed = FALSE
            AND rs.completed = FALSE
            AND ((rs.start >= ?2 AND rs.start < ?3)
            OR (rs.finish > ?2 AND rs.finish <= ?3)
            OR (rs.start <= ?2 AND rs.finish >= ?3))
            """)
    List<Recording> findByBoxIdAndRemovedIsFalse(Integer boxId, LocalDateTime start, LocalDateTime finish);

    /**
     * Возвращает список неотмененных записей по идентификатору бокса за заданный диапазон даты, времени,
     * кроме записи с идентификатором изменяемой.
     *
     * @param boxId  Идентификатор бокса
     * @param start  Начало диапазона
     * @param finish Окончание диапазона
     * @param id     Идентификатор записи
     * @return Список записей
     */
    @Query(value = """
            SELECT rs FROM Recording rs
            WHERE rs.box.id = ?1
            AND rs.removed = FALSE
            AND rs.completed = FALSE
            AND rs.id!=?4
            AND ((rs.start >= ?2 AND rs.start < ?3)
            OR (rs.finish > ?2 AND rs.finish <= ?3)
            OR (rs.start <= ?2 AND rs.finish >= ?3))
            """)
    List<Recording> findByIdAndBoxIdAndRemovedIsFalse(Integer boxId, LocalDateTime start, LocalDateTime finish, Integer id);

    /**
     * Возвращает все неотмененные записи пользователя.
     */
    List<Recording> findAllByUserAndRemovedIsFalse(User user);

    /**
     * Возвращает список активных броней по идентификатору пользователя.
     *
     * @param userId Идентификатор пользователя
     */
    List<Recording> findAllByUser_IdAndReservedIsTrueAndCompletedIsFalse(Integer userId);

    /**
     * Возвращает список выполненных записей по идентификатору пользователя.
     *
     * @param userId Идентификатор пользователя
     */
    List<Recording> findAllByUser_IdAndCompletedIsTrue(Integer userId);

    /**
     * Возвращает неподтвержденную запись по ее идентификатору, если она еще не удалена.
     *
     * @param id Идентификатор записи
     * @return Запись.
     */
    Optional<Recording> findByIdAndReservedIsFalseAndRemovedIsFalse(Integer id);

    /**
     * Возвращает список неподтвержденных записей за заданный диапазон
     */
    @Query(value =
            """
            SELECT rs FROM Recording rs
            WHERE rs.removed = FALSE
            AND rs.reserved = FALSE
            AND rs.created <= ?1
                    """
    )
    List<Recording> findAllByBetween(LocalDateTime checkTime);
}
