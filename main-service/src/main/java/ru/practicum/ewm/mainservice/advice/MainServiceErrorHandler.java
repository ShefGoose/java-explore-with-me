package ru.practicum.ewm.mainservice.advice;

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
import ru.practicum.ewm.mainservice.advice.exception.DuplicateEmailException;
import ru.practicum.ewm.mainservice.advice.exception.DuplicationNameCatException;
import ru.practicum.ewm.mainservice.advice.exception.EntityNotFoundException;
import ru.practicum.ewm.mainservice.advice.exception.EventConflictException;
import ru.practicum.ewm.mainservice.advice.response.ApiError;
import ru.practicum.ewm.mainservice.advice.response.ValidationErrorResponse;
import ru.practicum.ewm.mainservice.advice.response.Violation;
import ru.practicum.ewm.statsclient.advice.exception.StatsServiceException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice(basePackages = {"ru.practicum.ewm.mainservice.user.controller",
        "ru.practicum.ewm.mainservice.category.controller",
        "ru.practicum.ewm.mainservice.event.controller",
        "ru.practicum.ewm.mainservice.request.controller",
        "ru.practicum.ewm.mainservice.compilation.controller"})
public class MainServiceErrorHandler extends ResponseEntityExceptionHandler {
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

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateEmailException.class)
    public ApiError handleDuplicateEmailException(final DuplicateEmailException e) {
        return new ApiError(HttpStatus.CONFLICT, "Integrity constraint has been violated.",
                e.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicationNameCatException.class)
    public ApiError handleDuplicationNameCatException(final DuplicationNameCatException e) {
        return new ApiError(HttpStatus.CONFLICT, "Integrity constraint has been violated.",
                e.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EventConflictException.class)
    public ApiError handleEventConflictException(final EventConflictException e) {
        return new ApiError(HttpStatus.CONFLICT, "For the requested operation the conditions are not met.",
                e.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ApiError handleEntityNotFoundException(final EntityNotFoundException e) {
        return new ApiError(HttpStatus.NOT_FOUND, "The required object was not found.",
                e.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(StatsServiceException.class)
    public ApiError handleStatsServiceException(final StatsServiceException e) {
        return new ApiError(HttpStatus.SERVICE_UNAVAILABLE,
                "Statistics service is unavailable", e.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiError handleAll(final Exception ex, final WebRequest request) {
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), "Произошла ошибка");
    }
}
