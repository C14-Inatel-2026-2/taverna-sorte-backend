package com.tavernasorte.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateUserResponse(UUID id, String name, String email, LocalDateTime createdAt) {
}
