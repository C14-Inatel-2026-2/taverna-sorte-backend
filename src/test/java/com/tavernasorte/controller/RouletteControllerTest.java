package com.tavernasorte.controller;

import com.tavernasorte.dto.RouletteHistoryResponse;
import com.tavernasorte.dto.RoulettePlayResponse;
import com.tavernasorte.exception.GlobalExceptionHandler;
import com.tavernasorte.model.RouletteBetType;
import com.tavernasorte.service.RouletteHistoryService;
import com.tavernasorte.service.RouletteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RouletteControllerTest {

    @Mock private RouletteService roulette;
    @Mock private RouletteHistoryService history;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new RouletteController(roulette, history))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void shouldPlayAndRecordUsingPlayerFromPath() throws Exception {
        var result = new RoulettePlayResponse(RouletteBetType.NUMBER, 0, 10, 0, "GREEN", true, 360);
        when(history.play(1L, RouletteBetType.NUMBER, 0, 10)).thenReturn(result);

        mvc.perform(post("/roleta/1/jogar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"betType\":\"NUMBER\",\"chosenNumber\":0,\"betAmount\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.drawnNumber").value(0))
                .andExpect(jsonPath("$.color").value("GREEN"))
                .andExpect(jsonPath("$.won").value(true))
                .andExpect(jsonPath("$.prize").value(360));

        verify(history).play(1L, RouletteBetType.NUMBER, 0, 10);
    }

    @Test
    void shouldReturnHistoryWithoutExposingPlayerEntity() throws Exception {
        var result = new RoulettePlayResponse(RouletteBetType.RED, null, 10, 1, "RED", true, 20);
        when(history.findByPlayer(1L)).thenReturn(List.of(
                new RouletteHistoryResponse(4L, Instant.parse("2026-11-12T12:00:00Z"), result)));

        mvc.perform(get("/roleta/1/historico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(4))
                .andExpect(jsonPath("$[0].playedAt").exists())
                .andExpect(jsonPath("$[0].result.betAmount").value(10))
                .andExpect(jsonPath("$[0].player").doesNotExist());

        verify(history).findByPlayer(1L);
    }

    @Test
    void shouldReturnEmptyArrayWhenHistoryIsEmpty() throws Exception {
        when(history.findByPlayer(1L)).thenReturn(List.of());

        mvc.perform(get("/roleta/1/historico"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test
    void shouldReturnNotFoundForUnknownPlayer() throws Exception {
        when(history.findByPlayer(99L)).thenThrow(new RouletteHistoryService.PlayerNotFoundException());

        mvc.perform(get("/roleta/99/historico"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Jogador não encontrado"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}", "{\"betType\":\"RED\",\"betAmount\":0}",
            "{\"betType\":\"RED\",\"betAmount\":1.5}",
            "{\"betType\":\"NUMBER\",\"chosenNumber\":1.5,\"betAmount\":10}",
            "{\"betType\":\"RED\",\"betAmount\":2147483648}",
            "{\"betType\":\"RED\",\"betAmount\":\"10\"}"
    })
    void shouldRejectInvalidRequestBeforeCallingService(String body) throws Exception {
        mvc.perform(post("/roleta/1/jogar").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isString());

        verifyNoInteractions(history, roulette);
    }

    @Test
    void shouldKeepLegacySpinEndpoint() throws Exception {
        when(roulette.spin()).thenReturn(36);

        mvc.perform(get("/roleta/girar"))
                .andExpect(status().isOk()).andExpect(content().string("36"));

        verifyNoInteractions(history);
    }
}
