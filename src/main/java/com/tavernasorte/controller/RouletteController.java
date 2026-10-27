package com.tavernasorte.controller;

import com.tavernasorte.service.RouletteService;
import org.springframework.web.bind.annotation.GetMapping;
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
}
