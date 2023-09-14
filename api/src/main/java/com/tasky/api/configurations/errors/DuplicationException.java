package com.tasky.api.configurations.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception representing a conflict due to duplication error (HTTP 409).
 */
@ResponseStatus(code = HttpStatus.CONFLICT)
public class DuplicationException extends RuntimeException {
    /**
     * Constructs a new `DuplicationException` instance with the provided error message.
     *
     * @param message The error message describing the nature of the duplication conflict.
     */
    public DuplicationException(String message) {
        super(message);
    }
}
