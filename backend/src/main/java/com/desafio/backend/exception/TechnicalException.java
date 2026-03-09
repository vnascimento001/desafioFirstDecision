package com.desafio.backend.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;

public class TechnicalException extends AppException {

    private static final long serialVersionUID = 1L;
	private final HttpStatus status;

    public TechnicalException(String message) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR, null, null);
    }

    public TechnicalException(String message, Throwable cause) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR, null, cause);
    }

    public TechnicalException(String message, HttpStatus status, Map<String, String> details, Throwable cause) {
        super(message, details, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
