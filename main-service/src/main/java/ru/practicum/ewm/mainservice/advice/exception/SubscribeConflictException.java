package ru.practicum.ewm.mainservice.advice.exception;

public class SubscribeConflictException extends RuntimeException {
    public SubscribeConflictException(String message) {
        super(message);
    }
}
