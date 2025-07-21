package ru.practicum.ewm.mainservice.advice;

import org.junit.jupiter.api.Test;
import ru.practicum.ewm.mainservice.advice.validation.EventDateValidator;
import ru.practicum.ewm.mainservice.advice.validation.NullOrNotBlankValidator;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdviceValidationTest {
    private final EventDateValidator eventValidator = new EventDateValidator();
    private final NullOrNotBlankValidator nullOrBlankValidator = new NullOrNotBlankValidator();

    @Test
    void futureDateAfter2HoursIsValid() {
        assertTrue(eventValidator.isValid(LocalDateTime.now().plusHours(3), null));
    }

    @Test
    void dateSoonerThan2HoursIsInvalid() {
        assertFalse(eventValidator.isValid(LocalDateTime.now().plusMinutes(30), null));
    }

    @Test
    void nullValueIsValid() {
        assertTrue(nullOrBlankValidator.isValid(null, null));
    }

    @Test
    void blankStringIsInvalid() {
        assertFalse(nullOrBlankValidator.isValid("   ", null));
    }

    @Test
    void normalStringIsValid() {
        assertTrue(nullOrBlankValidator.isValid("text", null));
    }
}
