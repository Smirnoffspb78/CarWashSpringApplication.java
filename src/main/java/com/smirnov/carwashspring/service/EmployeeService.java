package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.entity.users.Employee;
import com.smirnov.carwashspring.exception.EntityNotFoundException;
import com.smirnov.carwashspring.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    /**
     * Скидка оператора.
     */
    private final EmployeeRepository employeeRepository;

    /** Инициализирует скидку для нового оператора.
     *
     * @param employee скидка
     */
    public Integer saveEmployee(Employee employee) {
        return employeeRepository.save(employee).getId();
    }

    /**
     * Возвращает скидку оператора по ее идентификатору.
     * @param id Идентификатор скидки
     * @return Скидка
     */
    public Employee getEmployeeById(Integer id) {
        Employee employee =  employeeRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(Employee.class, id));
        log.info("Получена скидка с id {}", id);
        return employee;
    }
}
