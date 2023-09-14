package com.tasky.api.configurations.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * Custom exception representing a resource not found error (HTTP 404).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    /**
     * Constructs a new `NotFoundException` instance with the provided error message.
     *
     * @param message The error message indicating that the requested resource was not found.
     */
    public NotFoundException(String message) {
        super(message);
    }
}
