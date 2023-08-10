package com.tasky.api.configurations.errors;
import org.springframework.http.HttpStatus;
import java.time.ZonedDateTime;

/**
 * Represents an API exception with detailed information about the error.
 */
public record ApiException(String message, HttpStatus httpStatus, ZonedDateTime zonedDateTime) {
}
