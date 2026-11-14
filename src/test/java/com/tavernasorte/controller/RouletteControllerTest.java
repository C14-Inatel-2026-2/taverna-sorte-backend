package com.tavernasorte.controller;

import com.tavernasorte.database.entity.Player;
import com.tavernasorte.dto.PlayerResponse;
import com.tavernasorte.dto.RoulettePlayResponse;
import com.tavernasorte.exception.GlobalExceptionHandler;
import com.tavernasorte.exception.InvalidBetException;
import com.tavernasorte.exception.PlayerNotFoundException;
import com.tavernasorte.model.RouletteBetType;
import com.tavernasorte.service.PlayerService;
import com.tavernasorte.service.RouletteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RouletteControllerTest {

    @Mock
    private RouletteService roulette;

    @Mock
    private PlayerService players;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new RouletteController(roulette, players))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void shouldPlayAfterValidatingPlayerFromPath() throws Exception {
        PlayerResponse player = PlayerResponse.from(new Player("Chris"));
        var result = new RoulettePlayResponse(RouletteBetType.NUMBER, 0, 10, 0, "GREEN", true, 360);
        when(players.findById(1L)).thenReturn(player);
        when(roulette.play(RouletteBetType.NUMBER, 0, 10)).thenReturn(result);

        mvc.perform(post("/roleta/1/jogar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"betType\":\"NUMBER\",\"chosenNumber\":0,\"betAmount\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.betType").value("NUMBER"))
                .andExpect(jsonPath("$.chosenNumber").value(0))
                .andExpect(jsonPath("$.betAmount").value(10))
                .andExpect(jsonPath("$.drawnNumber").value(0))
                .andExpect(jsonPath("$.color").value("GREEN"))
                .andExpect(jsonPath("$.won").value(true))
                .andExpect(jsonPath("$.prize").value(360));

        InOrder order = inOrder(players, roulette);
        order.verify(players).findById(1L);
        order.verify(roulette).play(RouletteBetType.NUMBER, 0, 10);
    }

    @Test
    void shouldReturnNotFoundForUnknownPlayerBeforePlaying() throws Exception {
        when(players.findById(99L)).thenThrow(new PlayerNotFoundException());

        mvc.perform(post("/roleta/99/jogar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"betType\":\"RED\",\"betAmount\":10}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Jogador não encontrado"));

        verifyNoInteractions(roulette);
    }

    @Test
    void shouldReturnBadRequestForInvalidBet() throws Exception {
        when(players.findById(1L)).thenReturn(PlayerResponse.from(new Player("Chris")));
        when(roulette.play(RouletteBetType.NUMBER, 37, 10))
                .thenThrow(new InvalidBetException("O número escolhido deve estar entre 0 e 36"));

        mvc.perform(post("/roleta/1/jogar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"betType\":\"NUMBER\",\"chosenNumber\":37,\"betAmount\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("O número escolhido deve estar entre 0 e 36"));
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

        verifyNoInteractions(players, roulette);
    }

    @Test
    void shouldKeepLegacySpinEndpoint() throws Exception {
        when(roulette.spin()).thenReturn(36);

        mvc.perform(get("/roleta/girar"))
                .andExpect(status().isOk())
                .andExpect(content().string("36"));

        verify(roulette).spin();
        verifyNoInteractions(players);
    }
}
