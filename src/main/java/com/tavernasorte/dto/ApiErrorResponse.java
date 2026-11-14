package com.tavernasorte.dto;

import java.util.Objects;

public record ApiErrorResponse(String error) {

    public ApiErrorResponse {
        Objects.requireNonNull(error);
    }
}
