package com.smirnov.carwashspring.repository;

import com.smirnov.carwashspring.entity.Box;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий бокса.
 */
@Repository
public interface BoxRepository extends CrudRepository<Box, Integer> {
}
