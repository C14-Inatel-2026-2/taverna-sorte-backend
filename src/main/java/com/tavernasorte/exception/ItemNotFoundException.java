package com.tavernasorte.exception;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException() {
        super("Item não encontrado");
    }
}
