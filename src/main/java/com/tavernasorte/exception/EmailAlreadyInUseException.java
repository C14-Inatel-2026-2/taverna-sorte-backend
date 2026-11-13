package com.tavernasorte.exception;

public class EmailAlreadyInUseException extends RuntimeException {

    public EmailAlreadyInUseException() {
        super("Email já está em uso.");
    }
}
