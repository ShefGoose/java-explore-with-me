package ru.practicum.ewm.statsclient.advice.exception;

public class StatsServiceException extends RuntimeException {
    public StatsServiceException(String message) {
        super(message);
    }

    public StatsServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
