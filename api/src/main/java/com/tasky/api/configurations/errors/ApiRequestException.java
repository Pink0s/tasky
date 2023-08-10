package com.tasky.api.configurations.errors;

public class ApiRequestException extends RuntimeException {
    /**
     * Constructs a new ApiRequestException with the specified error message.
     *
     * @param message The error message.
     */
    public ApiRequestException(String message) {
        super(message);
    }
    /**
     * Constructs a new ApiRequestException with the specified error message and cause.
     *
     * @param message The error message.
     * @param cause The cause of the exception.
     */
    public ApiRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
