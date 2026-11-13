package com.tavernasorte.controller;

import com.tavernasorte.dto.CreatePlayerRequest;
import com.tavernasorte.dto.PlayerResponse;
import com.tavernasorte.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerControllerTest {

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private PlayerController playerController;

    @Test
    void shouldReturnPlayerWhenItExists() {
        PlayerResponse player = mock(PlayerResponse.class);

        when(playerService.findById(1L)).thenReturn(player);

        ResponseEntity<PlayerResponse> response = playerController.getPlayer(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturnNotFoundWhenPlayerDoesNotExist() {
        when(playerService.findById(1L)).thenReturn(null);

        ResponseEntity<PlayerResponse> response = playerController.getPlayer(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldReturnCreatedWhenPlayerIsCreated() {
        CreatePlayerRequest request = mock(CreatePlayerRequest.class);
        PlayerResponse player = mock(PlayerResponse.class);

        when(request.getUsername()).thenReturn("Chris Careca");
        when(playerService.create("Chris Careca")).thenReturn(player);

        ResponseEntity<PlayerResponse> response = playerController.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void shouldReturnOkWhenDanceForGnomeSucceeds() {
        when(playerService.danceForGnome(1L)).thenReturn(true);

        ResponseEntity<Void> response = playerController.danceForGnome(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturnBadRequestWhenDanceForGnomeFails() {
        when(playerService.danceForGnome(1L)).thenReturn(false);

        ResponseEntity<Void> response = playerController.danceForGnome(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}