package com.tavernasorte.controller;

import com.tavernasorte.dto.ApostaBauRequest;
import com.tavernasorte.dto.ResultadoBau;
import com.tavernasorte.service.BauEncantadoService;
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
    public ResultadoBau abrir(@RequestBody ApostaBauRequest aposta) {
        return bauEncantadoService.abrir(aposta.getValor());
    }
}
