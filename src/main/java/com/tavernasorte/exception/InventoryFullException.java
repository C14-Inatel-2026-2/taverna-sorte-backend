package com.tavernasorte.exception;

public class InventoryFullException extends RuntimeException {

    public InventoryFullException() {
        super("O inventário está cheio");
    }
}
