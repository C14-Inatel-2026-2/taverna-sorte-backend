package com.tavernasorte.controller;

import com.tavernasorte.service.RouletteService;
import com.tavernasorte.dto.RoulettePlayRequest;
import com.tavernasorte.dto.RoulettePlayResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RouletteController {

    private final RouletteService rouletteService;

    public RouletteController(RouletteService rouletteService) {
        this.rouletteService = rouletteService;
    }

    @GetMapping("/roleta/girar")
    public int spin() {
        return rouletteService.spin();
    }

    @PostMapping("/roleta/jogar")
    public RoulettePlayResponse play(@Valid @RequestBody RoulettePlayRequest request) {
        return rouletteService.play(request.chosenNumber(), request.betAmount());
    }
}
