package com.tavernasorte.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserResponse {

    private final UUID id;
    private final String name;
    private final String email;
    private final LocalDateTime createdAt;

    public UserResponse(UUID id, String name, String email, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}