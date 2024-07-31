package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.entity.users.Employee;
import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.enums.TypeDiscount;
import com.smirnov.carwashspring.exception.EntityNotFoundException;
import com.smirnov.carwashspring.repository.EmployeeRepository;
import com.smirnov.carwashspring.service.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {



    /**
     * Репозиторий работников.
     */
    private final EmployeeRepository employeeRepository;

    /**
     * Сервисный слой пользователей.
     */
    private final UserService userService;

    /**
     * Обновляет минимальную или максимальную скидку, предоставляемую оператором.
     * Уровень доступа: ADMIN.
     *
     * @param id           Идентификатор работника
     * @param discount     Скидка
     * @param typeDiscount Тип скидки, принимает значения min или max.
     */
    public void updateDiscountForUser(Integer id,
                                      int discount,
                                      TypeDiscount typeDiscount) {
        Employee employee = getEmployeeById(id);
        switch (typeDiscount) {
            case MIN -> {
                if (discount > employee.getMaxDiscountForUsers()) {
                    throw new IllegalArgumentException("minDiscountForUsers не должен быть больше, чем max");
                }
                employee.setMinDiscountForUsers(discount);
            }
            case MAX -> {
                if (discount < employee.getMinDiscountForUsers()) {
                    throw new IllegalArgumentException("minDiscountForUsers не должен быть больше, чем max");
                }
                employee.setMaxDiscountForUsers(discount);
            }
        }
        log.info("{}. Оператору с id {} назначена скидка c {}. Размер скидки {}", HttpStatus.NO_CONTENT, id, typeDiscount, discount);
    }

    /**
     * Назначает скидку пользователю по идентификатору
     *
     * @param discount   Размер скидки, [%]
     * @param userId     Идентификатор пользователя
     */
    public void activateDiscount(int discount, Integer userId, Integer operatorId) {
        User user = userService.getUserById(userId);
        Employee employee = getEmployeeById(operatorId);
        if (discount > employee.getMaxDiscountForUsers() || discount < employee.getMinDiscountForUsers()) {
            throw new IllegalArgumentException("discount должен быть в диапазоне от MinDiscount до MaxDiscount");
        }
        user.setDiscount(discount);
        log.info("Пользователю с id {} назначена скидка {}% оператором с id {}", userId, discount, operatorId);
    }

    /**
     * Удаляет скидку пользователю по его идентификатору.
     *
     * @param id Идентификатор пользователя.
     */
    public void deactivateDiscount(Integer id) {
        User user = userService.getUserById(id);
        user.setDiscount(0);
        log.info("Скидка у пользователя с id {} удалена", id);
    }

    /**
     * Вспомогательный метод. Возвращает работника по его идентификатору.
     * @param id Идентификатор скидки
     * @return Скидка
     */
    public Employee getEmployeeById(Integer id) {
        Employee employee =  employeeRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(Employee.class, id));
        log.info("Получен работник с id {}", id);
        return employee;
    }
}
