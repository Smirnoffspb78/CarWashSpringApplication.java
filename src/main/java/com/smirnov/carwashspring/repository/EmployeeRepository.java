package com.smirnov.carwashspring.repository;

import com.smirnov.carwashspring.entity.users.Employee;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeRepository extends CrudRepository<Employee, Integer> {
}
