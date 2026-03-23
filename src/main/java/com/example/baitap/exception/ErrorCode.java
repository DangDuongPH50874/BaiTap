package com.example.baitap.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    VALIDATION_ERROR(400, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR"),
    NOT_FOUND(404, HttpStatus.NOT_FOUND, "NOT_FOUND"),
    FORBIDDEN(403, HttpStatus.FORBIDDEN, "FORBIDDEN"),
    CONFLICT(409, HttpStatus.CONFLICT, "CONFLICT"),
    BUSINESS_RULE(400, HttpStatus.BAD_REQUEST, "BUSINESS_RULE"),
    UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");

    private final int httpCode;
    private final HttpStatus httpStatus;
    private final String key;

    ErrorCode(int httpCode, HttpStatus httpStatus, String key) {
        this.httpCode = httpCode;
        this.httpStatus = httpStatus;
        this.key = key;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getKey() {
        return key;
    }
}

