package com.tasky.api.configurations.errors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

/**
 * Global exception handler for handling API-related exceptions.
 */
@ControllerAdvice
public class ApiExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /**
     * Handles ApiRequestException and returns an appropriate response.
     */
    @ExceptionHandler(value = ApiRequestException.class)
    public ResponseEntity<Object> handleApiRequestException(
            ApiRequestException e,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        ApiException apiException = new ApiException(
                e.getMessage(),
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now()
        );
        logger.error(apiException.toString());
        return new ResponseEntity<>(
                apiException,
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles InsufficientAuthenticationException and returns an appropriate response.
     */
    @ExceptionHandler(value = InsufficientAuthenticationException.class)
    public ResponseEntity<Object> handleApiRequestException(
            InsufficientAuthenticationException e,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        ApiException apiException = new ApiException(
                e.getMessage(),
                HttpStatus.FORBIDDEN,
                ZonedDateTime.now()
        );
        logger.error(apiException.toString());
        return new ResponseEntity<>(
                apiException,
                HttpStatus.FORBIDDEN);
    }

    /**
     * Handles BadRequestException and returns an appropriate response.
     */
    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<Object> handleApiRequestException(
            BadRequestException e,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        ApiException apiException = new ApiException(
                e.getMessage(),
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now()
        );
        logger.error(apiException.toString());
        return new ResponseEntity<>(
                apiException,
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles BadRequestException and returns an appropriate response.
     */
    @ExceptionHandler(value = DuplicationException.class)
    public ResponseEntity<Object> handleApiRequestException(
            DuplicationException e,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        ApiException apiException = new ApiException(
                e.getMessage(),
                HttpStatus.CONFLICT,
                ZonedDateTime.now()
        );

        logger.error(apiException.toString());

        return new ResponseEntity<>(
                apiException,
                HttpStatus.CONFLICT);
    }

    /**
     * Handles NotFound and returns an appropriate response.
     */
    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<Object> handleApiRequestException(
            NotFoundException e,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        ApiException apiException = new ApiException(
                e.getMessage(),
                HttpStatus.NOT_FOUND,
                ZonedDateTime.now()
        );

        logger.error(apiException.toString());

        return new ResponseEntity<>(
                apiException,
                HttpStatus.NOT_FOUND);
    }

    /**
     * Handles BadCredentialsException and returns an appropriate response.
     * */
    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<Object> handleApiRequestException(
            BadCredentialsException e,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        ApiException apiException = new ApiException(
                e.getMessage(),
                HttpStatus.UNAUTHORIZED,
                ZonedDateTime.now()
        );
        logger.error(apiException.toString());
        return new ResponseEntity<>(
                apiException,
                HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles HttpMessageNotReadableException and returns an appropriate response.
     * */

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleException(
            HttpMessageNotReadableException e,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        ApiException apiException = new ApiException(
                e.getMessage(),
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now()
        );
        logger.error(apiException.toString());
        return new ResponseEntity<>(
                apiException,
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles HttpMessageNotReadableException and returns an appropriate response.
     * */

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleException(
            UsernameNotFoundException e,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        ApiException apiException = new ApiException(
                e.getMessage(),
                HttpStatus.UNAUTHORIZED,
                ZonedDateTime.now()
        );
        logger.error(apiException.toString());
        return new ResponseEntity<>(
                apiException,
                HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles Exception and returns an appropriate response.
     * */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(
            Exception e,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        ApiException apiException = new ApiException(
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ZonedDateTime.now()
        );
        logger.error(apiException.toString());
        return new ResponseEntity<>(
                apiException,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
