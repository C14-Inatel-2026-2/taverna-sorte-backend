package com.tavernasorte.controller;

import com.tavernasorte.service.RoletaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoletaController {

    private final RoletaService roletaService;

    public RoletaController(RoletaService roletaService) {
        this.roletaService = roletaService;
    }

    @GetMapping("/roleta/girar")
    public int girar() {
        return roletaService.girar();
    }
}
