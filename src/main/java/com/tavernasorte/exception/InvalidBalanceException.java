package com.tavernasorte.exception;

public class InvalidBalanceException extends IllegalArgumentException {

    public InvalidBalanceException(String message) {
        super(message);
    }
}
