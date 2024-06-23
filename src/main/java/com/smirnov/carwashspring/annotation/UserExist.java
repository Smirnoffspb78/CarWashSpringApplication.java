package com.smirnov.carwashspring.annotation;

import com.smirnov.carwashspring.annotation.validator.RangeLocalDateTimeValidator;
import com.smirnov.carwashspring.annotation.validator.UserExistValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, TYPE, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = UserExistValidator.class)
public @interface UserExist {
    String message() default "Пользователь с id не существует";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
