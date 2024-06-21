package com.smirnov.carwashspring.repository;

import com.smirnov.carwashspring.entity.Box;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий бокса.
 */
@Repository
public interface BoxRepository extends CrudRepository<Box, Integer> {

    @NonNull
    List<Box> findAll();

    /*List<Box> getFreeBox(int id);*/
}
