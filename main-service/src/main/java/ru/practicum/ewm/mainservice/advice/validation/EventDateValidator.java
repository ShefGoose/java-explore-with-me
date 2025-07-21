package ru.practicum.ewm.mainservice.advice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;


public class EventDateValidator implements ConstraintValidator<EventDateValidation, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime value,
                           ConstraintValidatorContext ctx) {
        if (value == null) {
            return true;
        }
        return value.isAfter(LocalDateTime.now().plusHours(2));
    }
}
