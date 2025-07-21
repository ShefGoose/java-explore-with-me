package ru.practicum.ewm.mainservice.advice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EventDateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventDateValidation {
    String message() default
            "Время начала события должно быть минимум через 2 часа";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
