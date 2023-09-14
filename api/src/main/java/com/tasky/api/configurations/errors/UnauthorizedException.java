package com.tasky.api.configurations.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception representing an unauthorized access error (HTTP 401).
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {
    /**
     * Constructs a new `UnauthorizedException` instance with the provided error message.
     *
     * @param message The error message indicating that the access is unauthorized.
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}
