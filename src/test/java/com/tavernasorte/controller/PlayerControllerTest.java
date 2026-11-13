package com.tavernasorte.controller;

import com.tavernasorte.dto.PlayerResponse;
import com.tavernasorte.exception.DanceNotAllowedException;
import com.tavernasorte.exception.GlobalExceptionHandler;
import com.tavernasorte.exception.PlayerNotFoundException;
import com.tavernasorte.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PlayerControllerTest {

    @Mock
    private PlayerService playerService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PlayerController(playerService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturnPlayerWhenItExists() throws Exception {
        PlayerResponse player = new PlayerResponse(1L, "Chris Careca", 100,
                new PlayerResponse.InventoryResponse(List.of(), 3));
        when(playerService.findById(1L)).thenReturn(player);

        mockMvc.perform(get("/players/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("Chris Careca"))
                .andExpect(jsonPath("$.balance").value(100));
    }

    @Test
    void shouldReturnNotFoundWhenPlayerDoesNotExist() throws Exception {
        when(playerService.findById(1L)).thenThrow(new PlayerNotFoundException());

        mockMvc.perform(get("/players/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Jogador não encontrado"));
    }

    @Test
    void shouldReturnCreatedWhenPlayerIsCreated() throws Exception {
        PlayerResponse player = new PlayerResponse(1L, "Chris Careca", 0,
                new PlayerResponse.InventoryResponse(List.of(), 3));
        when(playerService.create("Chris Careca")).thenReturn(player);

        mockMvc.perform(post("/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Chris Careca\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("Chris Careca"));

        verify(playerService).create("Chris Careca");
    }

    @Test
    void shouldReturnOkWhenDanceForGnomeSucceeds() throws Exception {
        mockMvc.perform(post("/players/1/dance"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(playerService).danceForGnome(1L);
    }

    @Test
    void shouldReturnBadRequestWhenDanceForGnomeFails() throws Exception {
        doThrow(new DanceNotAllowedException()).when(playerService).danceForGnome(1L);

        mockMvc.perform(post("/players/1/dance"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Só é possível dançar para o gnomo quando o saldo é zero"));
    }

    @Test
    void shouldReturnNotFoundWhenDancingForMissingPlayer() throws Exception {
        doThrow(new PlayerNotFoundException()).when(playerService).danceForGnome(1L);

        mockMvc.perform(post("/players/1/dance"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Jogador não encontrado"));
    }
}
