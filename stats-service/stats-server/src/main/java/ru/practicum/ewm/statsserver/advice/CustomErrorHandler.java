package ru.practicum.ewm.statsserver.advice;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.practicum.ewm.statsserver.advice.exception.BadDateException;
import ru.practicum.ewm.statsserver.advice.exception.BadIpException;
import ru.practicum.ewm.statsserver.advice.response.ApiError;
import ru.practicum.ewm.statsserver.advice.response.ValidationErrorResponse;
import ru.practicum.ewm.statsserver.advice.response.Violation;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomErrorHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                  final HttpHeaders headers,
                                                                  final HttpStatusCode status,
                                                                  final WebRequest request) {
        final List<Violation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse(violations);
        return handleExceptionInternal(ex, validationErrorResponse, headers, HttpStatus.BAD_REQUEST, request);
    }

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
