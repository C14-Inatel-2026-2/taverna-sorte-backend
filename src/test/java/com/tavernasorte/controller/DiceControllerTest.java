package com.tavernasorte.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.tavernasorte.dto.DicePlayResponse;
import com.tavernasorte.exception.GlobalExceptionHandler;
import com.tavernasorte.exception.InvalidBetException;
import com.tavernasorte.exception.PlayerNotFoundException;
import com.tavernasorte.model.BetType;
import com.tavernasorte.service.DiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DiceControllerTest {

    private static final String ROUTE = "/dice/play";

    @Mock
    private DiceService diceService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(
                Jackson2ObjectMapperBuilder.json()
                        .featuresToDisable(DeserializationFeature.ACCEPT_FLOAT_AS_INT)
                        .build());

        mockMvc = MockMvcBuilders.standaloneSetup(new DiceController(diceService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(jsonConverter)
                .build();
    }

    @Test
    void shouldPlayWithItemAndReturnResult() throws Exception {
        DicePlayResponse result = new DicePlayResponse(2, 3, 5, BetType.LOW, true, 40, 130, 3L, 2);
        when(diceService.play(1L, BetType.LOW, 10, 3L)).thenReturn(result);

        mockMvc.perform(post(ROUTE).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":1,\"betType\":\"LOW\",\"betAmount\":10,\"itemId\":3}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.die1").value(2))
                .andExpect(jsonPath("$.die2").value(3))
                .andExpect(jsonPath("$.sum").value(5))
                .andExpect(jsonPath("$.betType").value("LOW"))
                .andExpect(jsonPath("$.won").value(true))
                .andExpect(jsonPath("$.prize").value(40))
                .andExpect(jsonPath("$.balance").value(130))
                .andExpect(jsonPath("$.consumedItemId").value(3))
                .andExpect(jsonPath("$.itemMultiplier").value(2));

        verify(diceService).play(1L, BetType.LOW, 10, 3L);
    }

    @Test
    void shouldPlayWithoutItemAndReturnLoss() throws Exception {
        DicePlayResponse result = new DicePlayResponse(6, 6, 12, BetType.LOW, false, 0, 90, null, 1);
        when(diceService.play(1L, BetType.LOW, 10, null)).thenReturn(result);

        mockMvc.perform(post(ROUTE).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":1,\"betType\":\"LOW\",\"betAmount\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.won").value(false))
                .andExpect(jsonPath("$.prize").value(0))
                .andExpect(jsonPath("$.balance").value(90))
                .andExpect(jsonPath("$.consumedItemId").isEmpty())
                .andExpect(jsonPath("$.itemMultiplier").value(1));

        verify(diceService).play(1L, BetType.LOW, 10, null);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"betType\":\"LOW\",\"betAmount\":10}",
            "{\"playerId\":1,\"betAmount\":10}",
            "{\"playerId\":1,\"betType\":\"LOW\"}",
            "{\"playerId\":0,\"betType\":\"LOW\",\"betAmount\":10}",
            "{\"playerId\":1,\"betType\":\"LOW\",\"betAmount\":0}",
            "{\"playerId\":1,\"betType\":\"LOW\",\"betAmount\":10001}",
            "{\"playerId\":1,\"betType\":\"INVALID\",\"betAmount\":10}",
            "{\"playerId\":1,\"betType\":\"LOW\",\"betAmount\":10.5}",
            "{\"playerId\":1,\"betType\":\"LOW\",\"betAmount\":10,\"itemId\":0}",
            "{\"playerId\":1,\"betType\":\"LOW\",\"betAmount\":10,\"unknown\":1}"
    })
    void shouldRejectInvalidRequestBeforeCallingService(String body) throws Exception {
        mockMvc.perform(post(ROUTE).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isString());

        verifyNoInteractions(diceService);
    }

    @Test
    void shouldReturnNotFoundForUnknownPlayer() throws Exception {
        when(diceService.play(1L, BetType.LOW, 10, null)).thenThrow(new PlayerNotFoundException());

        mockMvc.perform(post(ROUTE).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":1,\"betType\":\"LOW\",\"betAmount\":10}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Jogador não encontrado"));
    }

    @Test
    void shouldReturnBadRequestForRefusedBet() throws Exception {
        when(diceService.play(1L, BetType.LOW, 10, null))
                .thenThrow(new InvalidBetException(DiceService.INSUFFICIENT_BALANCE));

        mockMvc.perform(post(ROUTE).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":1,\"betType\":\"LOW\",\"betAmount\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(DiceService.INSUFFICIENT_BALANCE));
    }
}
