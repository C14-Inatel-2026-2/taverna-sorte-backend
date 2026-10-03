package com.tavernasorte.controller;

import com.tavernasorte.dto.DicePlayRequest;
import com.tavernasorte.dto.DicePlayResponse;
import com.tavernasorte.service.DiceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dice")
public class DiceController {

    private final DiceService diceService;

    public DiceController(DiceService diceService) {
        this.diceService = diceService;
    }

    @PostMapping("/play")
    public DicePlayResponse play(@Valid @RequestBody DicePlayRequest request) {
        return diceService.play(request.getTipoAposta(), request.getValorAposta());
    }
}
