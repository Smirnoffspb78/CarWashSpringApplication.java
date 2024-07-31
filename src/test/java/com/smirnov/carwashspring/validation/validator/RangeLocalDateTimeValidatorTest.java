package com.smirnov.carwashspring.validation.validator;

import com.smirnov.carwashspring.dto.range.RangeDataTimeDTO;
import com.smirnov.carwashspring.dto.request.create.BoxCreateDTO;
import jakarta.validation.ClockProvider;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class RangeLocalDateTimeValidatorTest {

    RangeLocalDateTimeValidator rangeLocalDateTimeValidator;

    ConstraintValidatorContext context = new ConstraintValidatorContext() {
        @Override
        public void disableDefaultConstraintViolation() {

        }

        @Override
        public String getDefaultConstraintMessageTemplate() {
            return null;
        }

        @Override
        public ClockProvider getClockProvider() {
            return null;
        }

        @Override
        public ConstraintViolationBuilder buildConstraintViolationWithTemplate(String messageTemplate) {
            return null;
        }

        @Override
        public <T> T unwrap(Class<T> type) {
            return null;
        }
    };


    @BeforeEach
    void initializeValidator(){
        rangeLocalDateTimeValidator = new RangeLocalDateTimeValidator();
    }

    @Test
    void initialize() {
    }

    @Test
    void isValid_RangeLocalDateTimeNull_False() {
        boolean dataTimeNull = false;
        assertEquals(rangeLocalDateTimeValidator.isValid(null, context), dataTimeNull);
    }

    @Test
    void isValid_startNull_False() {
        RangeDataTimeDTO rangeDataTimeDTO = new RangeDataTimeDTO();
        rangeDataTimeDTO.setFinish(LocalDateTime.now());
        boolean dataTimeNull = false;
        assertEquals(rangeLocalDateTimeValidator.isValid(rangeDataTimeDTO, context), dataTimeNull);
    }

    @Test
    void isValid_finishNull_False() {
        RangeDataTimeDTO rangeDataTimeDTO = new RangeDataTimeDTO();
        rangeDataTimeDTO.setStart(LocalDateTime.now());
        boolean dataTimeNull = false;
        assertEquals(rangeLocalDateTimeValidator.isValid(rangeDataTimeDTO, context), false);
    }

    @Test
    void isValid_LocalDateTimeValid_True() {
        RangeDataTimeDTO rangeDataTimeDTO = new RangeDataTimeDTO();
        rangeDataTimeDTO.setStart(LocalDateTime.now());
        rangeDataTimeDTO.setFinish(LocalDateTime.now().plusMonths(40));
        boolean dataTimeValid = true;
        assertEquals(rangeLocalDateTimeValidator.isValid(rangeDataTimeDTO, context), dataTimeValid);
    }

    @Test
    void isValid_LocalDateTimeInValid_False() {
        RangeDataTimeDTO rangeDataTimeDTO = new RangeDataTimeDTO();
        rangeDataTimeDTO.setStart(LocalDateTime.now());
        rangeDataTimeDTO.setFinish(LocalDateTime.now().minusMinutes(40));
        boolean dataTimeValid = false;
        assertEquals(rangeLocalDateTimeValidator.isValid(rangeDataTimeDTO, context), dataTimeValid);
    }

    @Test
    void isValid_LocalTimeValid_True() {
        BoxCreateDTO boxCreateDTO = new BoxCreateDTO();
        boxCreateDTO.setStart(LocalTime.now());
        boxCreateDTO.setFinish(LocalTime.now().plusMinutes(40));
        boolean timeValid = true;
        assertEquals(rangeLocalDateTimeValidator.isValid(boxCreateDTO, context), timeValid);
    }
    @Test
    void isValid_LocalTimeInValid_False() {
        BoxCreateDTO boxCreateDTO = new BoxCreateDTO();
        boxCreateDTO.setStart(LocalTime.now());
        boxCreateDTO.setFinish(LocalTime.now().minusMinutes(40));
        boolean timeValid = false;
        assertEquals(rangeLocalDateTimeValidator.isValid(boxCreateDTO, context), timeValid);
    }


}