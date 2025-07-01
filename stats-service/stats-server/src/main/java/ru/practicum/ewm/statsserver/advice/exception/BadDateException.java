package ru.practicum.ewm.statsserver.advice.exception;

public class BadDateException extends RuntimeException {
    public BadDateException(String message) {
        super(message);
    }
}
