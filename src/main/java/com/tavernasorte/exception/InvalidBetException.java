package com.tavernasorte.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidBetException extends IllegalArgumentException {

    public InvalidBetException(String message) {
        super(message);
    }
}
