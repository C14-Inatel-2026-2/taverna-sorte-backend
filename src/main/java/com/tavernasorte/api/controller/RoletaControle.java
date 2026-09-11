package com.tavernasorte.api.controller;

import com.tavernasorte.api.service.RoletaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoletaControle {

    private final RoletaService roletaService;

    public RoletaControle(RoletaService roletaService) {
        this.roletaService = roletaService;
    }

    @GetMapping("/roleta/girar")
    public int girar() {
        return roletaService.girar();
    }
}