package com.tavernasorte.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "Nome é obrigatório") String name,
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido") String email,
        @NotBlank(message = "A senha deve ter no mínimo 6 caracteres")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres") String password
) {

    @Override
    public String toString() {
        return "CreateUserRequest[name=" + name + ", email=" + email + ", password=***]";
    }
}
