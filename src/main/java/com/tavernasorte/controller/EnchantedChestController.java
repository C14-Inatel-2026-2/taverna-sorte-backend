package com.tavernasorte.controller;

import com.tavernasorte.dto.EnchantedChestRequest;
import com.tavernasorte.dto.EnchantedChestResponse;
import com.tavernasorte.service.EnchantedChestService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnchantedChestController {

    private final EnchantedChestService bauEncantadoService;

    public EnchantedChestController(EnchantedChestService bauEncantadoService) {
        this.bauEncantadoService = bauEncantadoService;
    }

    @PostMapping("/bau/abrir")
    public EnchantedChestResponse abrir(@Valid @RequestBody EnchantedChestRequest request) {
        return bauEncantadoService.abrir(request.getValorAposta());
    }
}
