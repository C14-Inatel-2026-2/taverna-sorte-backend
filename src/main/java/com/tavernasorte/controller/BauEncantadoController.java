package com.tavernasorte.controller;

import com.tavernasorte.dto.BauEncantadoRequest;
import com.tavernasorte.dto.BauEncantadoResponse;
import com.tavernasorte.service.BauEncantadoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BauEncantadoController {

    private final BauEncantadoService bauEncantadoService;

    public BauEncantadoController(BauEncantadoService bauEncantadoService) {
        this.bauEncantadoService = bauEncantadoService;
    }

    @PostMapping("/bau/abrir")
    public BauEncantadoResponse abrir(@Valid @RequestBody BauEncantadoRequest request) {
        return bauEncantadoService.abrir(request.getValorAposta());
    }
}
