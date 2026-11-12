package com.tavernasorte.dto;

import java.time.Instant;

public record RouletteHistoryResponse(Long id, Instant playedAt, RoulettePlayResponse result) {
}
