package ru.practicum.ewm.mainservice.advice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NullOrNotBlankValidator.class)
public @interface NullOrNotBlank {
    String message() default "Поле не может состоять только из пробелов";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
