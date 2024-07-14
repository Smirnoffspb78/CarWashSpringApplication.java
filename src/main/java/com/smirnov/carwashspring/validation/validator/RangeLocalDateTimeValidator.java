package com.smirnov.carwashspring.validation.validator;

import com.smirnov.carwashspring.dto.request.create.BoxCreateDTO;
import com.smirnov.carwashspring.dto.range.SpecifyRange;
import com.smirnov.carwashspring.validation.RangeDateOrTime;
import com.smirnov.carwashspring.dto.range.RangeDataTimeDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Валидатор диапазона даты, времени.
 */
@Slf4j
public class RangeLocalDateTimeValidator implements ConstraintValidator<RangeDateOrTime, SpecifyRange> {

    private String message;

    @Override
    public void initialize(RangeDateOrTime constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(SpecifyRange value, ConstraintValidatorContext context) {
        if (value == null) {
            log.error("RangeDataTimeDTO or is null");
            context.disableDefaultConstraintViolation();
            return false;
        }
        if (value.getStart() == null || value.getFinish() == null) {
            return messageNull(context);
        }
        if (value.getStart() instanceof LocalTime startValue && value.getFinish() instanceof LocalTime finishValue){
            if (startValue.isAfter(finishValue)) {
                return messageInvalid(context);
            }
            return true;
        }
        if (value.getStart() instanceof LocalDateTime startValue && value.getFinish() instanceof LocalDateTime finishValue){
            if (startValue.isAfter(finishValue)) {
                return messageInvalid(context);
            }
            return true;
        }
        return false;
    }
    
    private boolean messageNull(ConstraintValidatorContext context){
        log.error("RangeDataTimeDTO or components is null");
        context.disableDefaultConstraintViolation();
        return false;
    }

    private boolean messageInvalid(ConstraintValidatorContext context){
        log.error(message);
        context.disableDefaultConstraintViolation();
        return false;
    }
}
