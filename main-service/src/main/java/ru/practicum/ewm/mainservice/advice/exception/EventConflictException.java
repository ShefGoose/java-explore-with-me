package ru.practicum.ewm.mainservice.advice.exception;

public class EventConflictException extends RuntimeException {
    public EventConflictException(String message) {
        super(message);
    }
}
