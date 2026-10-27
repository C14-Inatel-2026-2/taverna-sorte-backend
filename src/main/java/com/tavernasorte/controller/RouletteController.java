package com.tavernasorte.controller;

import com.tavernasorte.service.RouletteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RouletteController {

    private final RouletteService roletaService;

    public RouletteController(RouletteService roletaService) {
        this.roletaService = roletaService;
    }

    @GetMapping("/roleta/girar")
    public int girar() {
        return roletaService.girar();
    }
}
