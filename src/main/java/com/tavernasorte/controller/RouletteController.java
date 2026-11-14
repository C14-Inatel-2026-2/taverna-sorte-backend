package com.tavernasorte.controller;

import com.tavernasorte.dto.RoulettePlayRequest;
import com.tavernasorte.dto.RoulettePlayResponse;
import com.tavernasorte.dto.RouletteSpinResponse;
import com.tavernasorte.service.PlayerService;
import com.tavernasorte.service.RouletteService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RouletteController {

    private final RouletteService rouletteService;
    private final PlayerService playerService;

    public RouletteController(RouletteService rouletteService, PlayerService playerService) {
        this.rouletteService = rouletteService;
        this.playerService = playerService;
    }

    @GetMapping("/roleta/girar")
    public RouletteSpinResponse spin() {
        return new RouletteSpinResponse(rouletteService.spin());
    }

    @PostMapping("/roleta/{playerId}/jogar")
    public RoulettePlayResponse play(@PathVariable Long playerId,
                                     @Valid @RequestBody RoulettePlayRequest request) {
        playerService.findById(playerId);
        return rouletteService.play(request.betType(), request.chosenNumber(), request.betAmount());
    }
}
