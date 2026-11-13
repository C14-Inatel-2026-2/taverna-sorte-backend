package com.tavernasorte.controller;

import com.tavernasorte.dto.CreatePlayerRequest;
import com.tavernasorte.dto.PlayerResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import com.tavernasorte.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getPlayer(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PlayerResponse> create(@Valid @RequestBody CreatePlayerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playerService.create(request.getUsername()));
    }

    @PostMapping("/{id}/dance")
    public ResponseEntity<Void> danceForGnome(@PathVariable Long id) {
        playerService.danceForGnome(id);
        return ResponseEntity.ok().build();
    }
}
