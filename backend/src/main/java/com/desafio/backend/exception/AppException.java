package com.desafio.backend.exception;

import java.util.Map;

public abstract class AppException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final Map<String, String> details;

    protected AppException(String message) {
        this(message, null, null);
    }

    protected AppException(String message, Map<String, String> details) {
        this(message, details, null);
    }

    protected AppException(String message, Throwable cause) {
        this(message, null, cause);
    }

    protected AppException(String message, Map<String, String> details, Throwable cause) {
        super(message, cause);
        this.details = details;
    }

    public Map<String, String> getDetails() {
        return details;
    }
}
