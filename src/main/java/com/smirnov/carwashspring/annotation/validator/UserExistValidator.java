package com.smirnov.carwashspring.annotation.validator;

import com.smirnov.carwashspring.annotation.UserExist;
import com.smirnov.carwashspring.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RequiredArgsConstructor
public class UserExistValidator implements ConstraintValidator<UserExist, Integer> {

    private String message;
    @Autowired
    private final UserRepository userRepository;

    @Override
    public void initialize(UserExist constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            log.error("userId is null");
            context.disableDefaultConstraintViolation();
            return false;
        }
        if (!userRepository.existsById(value)){
            log.error(message);
            context.disableDefaultConstraintViolation();
            return false;
        }
        return true;
    }
}
