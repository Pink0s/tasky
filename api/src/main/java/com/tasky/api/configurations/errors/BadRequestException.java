package com.tasky.api.configurations.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception representing a bad request error (HTTP 400).
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    /**
     * Constructs a new `BadRequestException` instance with the provided error message.
     *
     * @param message The error message describing the nature of the bad request.
     */
    public BadRequestException(String message) {
        super(message);
    }
}
