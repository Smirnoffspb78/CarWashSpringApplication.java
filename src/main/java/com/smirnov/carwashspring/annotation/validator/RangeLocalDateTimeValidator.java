package com.smirnov.carwashspring.annotation.validator;

import com.smirnov.carwashspring.annotation.RangeLocalDateTime;
import com.smirnov.carwashspring.dto.RangeDataTimeDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Валидатор диапазона даты, времени.
 */
@Slf4j
public class RangeLocalDateTimeValidator implements ConstraintValidator<RangeLocalDateTime, RangeDataTimeDTO> {

    private String message;

    @Override
    public void initialize(RangeLocalDateTime constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(RangeDataTimeDTO rangeTimeDTO, ConstraintValidatorContext context) {
        if (rangeTimeDTO == null || rangeTimeDTO.start() == null || rangeTimeDTO.finish() == null) {
            log.error("RangeDataTimeDTO or components is null");
            context.disableDefaultConstraintViolation();
            return false;
        }
        if (rangeTimeDTO.start().isAfter(rangeTimeDTO.finish())) {
            log.error(message);
            context.disableDefaultConstraintViolation();
            return false;
        }
        return true;
    }
}
