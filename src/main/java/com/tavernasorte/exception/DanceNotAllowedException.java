package com.tavernasorte.exception;

public class DanceNotAllowedException extends RuntimeException {

    public DanceNotAllowedException() {
        super("Só é possível dançar para o gnomo quando o saldo é zero");
    }
}
