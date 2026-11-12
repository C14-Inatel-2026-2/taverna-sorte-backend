package com.tavernasorte.exception;

public class PlayerNotFoundException extends RuntimeException {

    public PlayerNotFoundException() {
        super("Jogador não encontrado");
    }
}
