package ru.practicum.ewm.statsserver.advice.exception;

public class BadIpException extends RuntimeException {
    public BadIpException(String message) {
        super(message);
    }
}
