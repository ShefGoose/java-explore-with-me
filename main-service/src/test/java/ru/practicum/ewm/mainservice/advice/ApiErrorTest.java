package ru.practicum.ewm.mainservice.advice;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.practicum.ewm.mainservice.advice.response.ApiError;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ApiErrorTest {
    @Test
    void apiErrorToStringNotNull() {
        ApiError err = new ApiError(HttpStatus.ACCEPTED,"msg","ok");
        assertNotNull(err.toString());
    }
}
