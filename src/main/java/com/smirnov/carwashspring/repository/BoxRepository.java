package com.smirnov.carwashspring.repository;

import com.smirnov.carwashspring.entity.service.Box;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий бокса.
 */
@Repository
public interface BoxRepository extends CrudRepository<Box, Integer> {

    /**
     * Возвращает список всех боксов
     * @return список всех боксов
     */
    @NonNull
    List<Box> findAll();

}
