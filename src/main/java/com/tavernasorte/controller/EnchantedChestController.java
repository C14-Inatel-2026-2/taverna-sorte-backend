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

    private final EnchantedChestService enchantedChestService;

    public EnchantedChestController(EnchantedChestService enchantedChestService) {
        this.enchantedChestService = enchantedChestService;
    }

    @PostMapping("/bau/abrir")
    public EnchantedChestResponse open(@Valid @RequestBody EnchantedChestRequest request) {
        return enchantedChestService.open(request.getBetAmount());
    }
}
