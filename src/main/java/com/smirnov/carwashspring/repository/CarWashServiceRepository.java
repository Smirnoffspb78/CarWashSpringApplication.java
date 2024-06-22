package com.smirnov.carwashspring.repository;

import com.smirnov.carwashspring.entity.service.CarWashService;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий услуг.
 */
@Repository
public interface CarWashServiceRepository extends CrudRepository<CarWashService, Integer> {

    /**
     * Возвращает список всех услуг
     * @return список услуг
     */
    @NonNull
    @Override
    List<CarWashService> findAll();

}
