package com.tavernasorte.controller;

import com.tavernasorte.exception.GlobalExceptionHandler;
import com.tavernasorte.service.RouletteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
                        .content("{\"numeroEscolhido\":7,\"valorAposta\":0.10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroEscolhido").value(7))
                .andExpect(jsonPath("$.numeroSorteado").value(7))
                .andExpect(jsonPath("$.valorAposta").value(0.10))
                .andExpect(jsonPath("$.venceu").value(true))
                .andExpect(content().string(containsString("\"premio\":0.90")));

        verify(service).spin();
    }

    @Test
    void shouldPlayLosingRound() throws Exception {
        doReturn(10).when(service).spin();

        mvc.perform(post("/roleta/jogar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numeroEscolhido\":1,\"valorAposta\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.venceu").value(false))
                .andExpect(jsonPath("$.numeroSorteado").value(10))
                .andExpect(content().string(containsString("\"premio\":0.00")));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}", "{\"numeroEscolhido\":1}", "{\"valorAposta\":10}",
            "{\"numeroEscolhido\":null,\"valorAposta\":10}",
            "{\"numeroEscolhido\":1,\"valorAposta\":null}",
            "{\"numeroEscolhido\":0,\"valorAposta\":10}",
            "{\"numeroEscolhido\":11,\"valorAposta\":10}",
            "{\"numeroEscolhido\":1,\"valorAposta\":0}",
            "{\"numeroEscolhido\":1,\"valorAposta\":-1}",
            "{\"numeroEscolhido\":1,\"valorAposta\":10000.01}",
            "{\"numeroEscolhido\":1,\"valorAposta\":0.001}",
            "{\"numeroEscolhido\":1,\"valorAposta\":\"abc\"}", "{", "null"
    })
    void shouldRejectInvalidRequestWithoutDrawing(String body) throws Exception {
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

    @Test
    void shouldHideUnexpectedFailure() throws Exception {
        doThrow(new IllegalStateException("detalhe interno")).when(service).spin();

        mvc.perform(post("/roleta/jogar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numeroEscolhido\":1,\"valorAposta\":10}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Erro interno do servidor"));
    }
}
