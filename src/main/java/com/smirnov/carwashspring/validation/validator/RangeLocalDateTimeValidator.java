package com.smirnov.carwashspring.validation.validator;

import com.smirnov.carwashspring.dto.request.create.BoxCreateDTO;
import com.smirnov.carwashspring.dto.range.Range;
import com.smirnov.carwashspring.validation.RangeDateOrTime;
import com.smirnov.carwashspring.dto.range.RangeDataTimeDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Валидатор диапазона даты, времени.
 */
@Slf4j
public class RangeLocalDateTimeValidator implements ConstraintValidator<RangeDateOrTime, Range> {

    private String message;

    @Override
    public void initialize(RangeDateOrTime constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Range value, ConstraintValidatorContext context) {
        if (value == null) {
            log.error("RangeDataTimeDTO or is null");
            context.disableDefaultConstraintViolation();
            return false;
        }
        if (value instanceof RangeDataTimeDTO rangeDataTimeDTO) {
            if (rangeDataTimeDTO.start() == null || rangeDataTimeDTO.finish() == null) {
                return messageNull(context);
            }
            if (rangeDataTimeDTO.start().isAfter(rangeDataTimeDTO.finish())) {
                return messageInvalid(context);
            }
            return true;
        }
        if (value instanceof BoxCreateDTO rangeTimeDTO) {
            if (rangeTimeDTO.getStart() == null || rangeTimeDTO.getFinish() == null) {
                return messageNull(context);
            }
            if (rangeTimeDTO.getStart().isAfter(rangeTimeDTO.getFinish())) {
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
