package com.tavernasorte.dto;

import jakarta.validation.constraints.NotBlank;

public class CreatePlayerRequest {

    @NotBlank(message = "O username é obrigatório")
    private String username;

    public CreatePlayerRequest() {
    }

    public CreatePlayerRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
