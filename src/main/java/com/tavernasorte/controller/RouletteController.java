package com.tavernasorte.controller;

import com.tavernasorte.service.RouletteService;
import com.tavernasorte.service.RouletteHistoryService;
import com.tavernasorte.dto.RouletteHistoryResponse;
import com.tavernasorte.dto.RoulettePlayRequest;
import com.tavernasorte.dto.RoulettePlayResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
