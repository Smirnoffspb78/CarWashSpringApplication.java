package com.smirnov.carwashspring.annotation;


import com.smirnov.carwashspring.annotation.validator.AfterLocalDateTimeValidate;
import com.smirnov.carwashspring.annotation.validator.RangeLocalDateTimeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Проверяет, чтобы начало диапазона даты, времени не было позднее окончания диапазона даты, времени.
 * null не является допустимым для объекта и его компонентов
 */
@Target({FIELD, TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = RangeLocalDateTimeValidator.class)
public @interface RangeLocalDateTime {

    String message() default "Окончание диапазона не должно быть позднее начала";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
