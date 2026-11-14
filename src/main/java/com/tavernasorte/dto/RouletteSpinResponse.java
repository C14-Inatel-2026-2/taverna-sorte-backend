package com.tavernasorte.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public record RouletteSpinResponse(int drawnNumber) {

    @JsonValue
    public int toJson() {
        return drawnNumber;
    }
}
