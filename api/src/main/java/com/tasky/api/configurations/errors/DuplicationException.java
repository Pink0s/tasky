package com.tasky.api.configurations.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class DuplicationException extends RuntimeException {

    public DuplicationException(String message) {
        super(message);
    }
}
