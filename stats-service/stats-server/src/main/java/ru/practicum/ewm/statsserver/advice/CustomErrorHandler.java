package ru.practicum.ewm.statsserver.advice;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.practicum.ewm.statsserver.advice.exception.BadDateException;
import ru.practicum.ewm.statsserver.advice.exception.BadIpException;
import ru.practicum.ewm.statsserver.advice.response.ApiError;

@RestControllerAdvice
public class CustomErrorHandler extends ResponseEntityExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ApiError handleValidationException(final ValidationException e) {
        return new ApiError(HttpStatus.BAD_REQUEST, "Ошибка валидации", e.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadIpException.class)
    public ApiError handleBadIpException(final BadIpException e) {
        return new ApiError(HttpStatus.BAD_REQUEST, "Неверный формат IP", e.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadDateException.class)
    public ApiError handleBadDateException(final BadDateException e) {
        return new ApiError(HttpStatus.BAD_REQUEST, "Ошибка даты", e.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiError handleAll(final Exception ex, final WebRequest request) {
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), "Произошла ошибка");
    }
}
