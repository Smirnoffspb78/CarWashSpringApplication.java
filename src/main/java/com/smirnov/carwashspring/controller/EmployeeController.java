package com.smirnov.carwashspring.controller;


import com.smirnov.carwashspring.enums.TypeDiscount;
import com.smirnov.carwashspring.service.EmployeeService;
import com.smirnov.carwashspring.service.security.JwtAuthenticationFilter;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/employees")
@Slf4j
@Validated
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * Изменяет скидку, предоставляемую оператором пользователю.
     * Права доступа: ADMIN.
     *
     * @param id           Идентификатор оператора
     * @param discount     Скидка
     * @param typeDiscount тип скидки: max или min, иначе выбрасывается исключение
     */
    @PutMapping("{id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    public void updateDiscountForUser(@PathVariable(name = "id") Integer id,
                                      @RequestParam("discount-for-user")
                                      @Range(min = 0, max = 100, message = "Скидка должна быть в диапазоне от 0 до 100")
                                      int discount,
                                      @RequestParam(name = "typeDiscount")
                                      TypeDiscount typeDiscount) {
        log.info("PUT: /employees/{}/{}/{}", id, discount, typeDiscount);
        employeeService.updateDiscountForUser(id, discount, typeDiscount);
    }

    /**
     * Назначает скидку пользователю.
     * Права доступа: ADMIN, OPERATOR.
     *
     * @param discount скидка
     * @param userId   идентификатор пользователя
     */
    @PutMapping("/{id}/{discount}/{employeeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_ADMIN", "ROLE_OPERATOR"})
    @PreAuthorize("authentication.principal.id == #employeeId")
    public void activateDiscountUser(@PathVariable(name = "discount") @Positive(message = "discount должен быть положительным") int discount,
                                     @PathVariable(name = "id") Integer userId,
                                     @PathVariable(name = "employeeId") Integer employeeId) {
        log.info("PUT: /employees/{}/{}", userId, discount);
        employeeService.activateDiscount(discount, userId, employeeId);
    }

    /**
     * Удаляет скидку пользователя по его идентификатору.
     * Уровень доступа: ADMIN, OPERATOR
     *
     * @param id Идентификатор пользователя
     */
    @PutMapping("/{id}/deactivate-discount")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_ADMIN", "ROLE_OPERATOR"})
    public void deactivateDiscountUser(@PathVariable(name = "id") Integer id) {
        log.info("PUT: /employees/{}/deactivate-discount", id);
        employeeService.deactivateDiscount(id);
    }
}
