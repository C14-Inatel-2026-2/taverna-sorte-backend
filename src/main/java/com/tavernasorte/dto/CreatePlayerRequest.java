package com.tavernasorte.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePlayerRequest(
        @NotBlank(message = "O username é obrigatório") String username
) {
}
