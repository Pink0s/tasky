package com.tasky.api.configurations.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {
    /**
     * @param message
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}
