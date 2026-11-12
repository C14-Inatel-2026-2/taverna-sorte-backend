package com.tavernasorte.controller;

import com.tavernasorte.exception.GlobalExceptionHandler;
import com.tavernasorte.service.RouletteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RouletteFlowTest {

    private RouletteService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        // O serviço real executa validação e cálculo; somente o sorteio é controlado.
        service = spy(new RouletteService());
        mvc = MockMvcBuilders.standaloneSetup(new RouletteController(service))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void shouldPlayWinningRoundWithExactDecimalPrize() throws Exception {
        doReturn(7).when(service).spin();

        mvc.perform(post("/roleta/jogar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"chosenNumber\":7,\"betAmount\":0.10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chosenNumber").value(7))
                .andExpect(jsonPath("$.drawnNumber").value(7))
                .andExpect(jsonPath("$.betAmount").value(0.10))
                .andExpect(jsonPath("$.won").value(true))
                .andExpect(content().string(containsString("\"prize\":0.90")));

        verify(service).spin();
    }

    @Test
    void shouldRejectInvalidRequestWithoutDrawing() throws Exception {
        String body = "{\"chosenNumber\":11,\"betAmount\":10}";
        mvc.perform(post("/roleta/jogar").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isString());

        verify(service, never()).spin();
    }

    @Test
    void shouldPreserveLegacySpinEndpoint() throws Exception {
        doReturn(3).when(service).spin();

        mvc.perform(get("/roleta/girar"))
                .andExpect(status().isOk()).andExpect(content().string("3"));
    }

}
