package com.desafio.backend.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;

public class BusinessException extends AppException {

    private static final long serialVersionUID = 1L;
	private final HttpStatus status;

    public BusinessException(String message) {
        this(message, HttpStatus.BAD_REQUEST, null);
    }

    public BusinessException(String message, HttpStatus status) {
        this(message, status, null);
    }

    public BusinessException(String message, HttpStatus status, Map<String, String> details) {
        super(message, details);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
