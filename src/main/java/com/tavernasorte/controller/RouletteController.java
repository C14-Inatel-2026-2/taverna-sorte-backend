package com.tavernasorte.controller;

import com.tavernasorte.service.RouletteService;
import com.tavernasorte.service.RouletteHistoryService;
import com.tavernasorte.dto.RouletteHistoryResponse;
import com.tavernasorte.dto.RoulettePlayRequest;
import com.tavernasorte.dto.RoulettePlayResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class RouletteController {

    private final RouletteService rouletteService;
    private final RouletteHistoryService historyService;

    public RouletteController(RouletteService rouletteService, RouletteHistoryService historyService) {
        this.rouletteService = rouletteService;
        this.historyService = historyService;
    }

    @GetMapping("/roleta/girar")
    public int spin() {
        return rouletteService.spin();
    }

    @PostMapping("/roleta/{playerId}/jogar")
    public RoulettePlayResponse play(@PathVariable Long playerId,
                                     @Valid @RequestBody RoulettePlayRequest request) {
        return historyService.play(playerId, request.betType(), request.chosenNumber(), request.betAmount());
    }

    @GetMapping("/roleta/{playerId}/historico")
    public List<RouletteHistoryResponse> history(@PathVariable Long playerId) {
        return historyService.findByPlayer(playerId);
    }

    @ExceptionHandler(RouletteHistoryService.PlayerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> playerNotFound(RouletteHistoryService.PlayerNotFoundException exception) {
        return Map.of("error", exception.getMessage());
    }
}
