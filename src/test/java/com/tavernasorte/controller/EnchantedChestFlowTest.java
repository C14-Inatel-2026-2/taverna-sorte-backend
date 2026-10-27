package com.tavernasorte.controller;

import com.tavernasorte.exception.GlobalExceptionHandler;
import com.tavernasorte.model.Symbol;
import com.tavernasorte.model.ChestBetValue;
import com.tavernasorte.service.EnchantedChestService;
import com.tavernasorte.service.SymbolPicker;
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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EnchantedChestFlowTest {

    @Mock
    private SymbolPicker sorteadorSymbol;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        EnchantedChestService service = new EnchantedChestService(sorteadorSymbol);
        mockMvc = MockMvcBuilders.standaloneSetup(new EnchantedChestController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"valorAposta", "valor"})
    void fluxoCompletoDevePagarDecimalExatoComAmbosOsNomesDoCampo(String campo) throws Exception {
        when(sorteadorSymbol.sortear()).thenReturn(Symbol.MOEDA);

        mockMvc.perform(post("/bau/abrir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"" + campo + "\":0.1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.simbolos").value(contains("MOEDA", "MOEDA", "MOEDA")))
                .andExpect(jsonPath("$.premio").isNumber())
                .andExpect(jsonPath("$.premio").value(0.3));

        verify(sorteadorSymbol, times(3)).sortear();
    }

    @ParameterizedTest
    @ValueSource(strings = {"valorAposta", "valor"})
    void apostaQueAntesGeravaInfinitoDeveSerRejeitadaAntesDoSorteio(String campo) throws Exception {
        mockMvc.perform(post("/bau/abrir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"" + campo + "\":1e308}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(ChestBetValue.FORMATO_INVALIDO));

        verifyNoInteractions(sorteadorSymbol);
    }

    @Test
    void maiorApostaPermitidaDeveRetornarPremioExatoComoNumeroJson() throws Exception {
        when(sorteadorSymbol.sortear()).thenReturn(Symbol.DRAGAO);

        mockMvc.perform(post("/bau/abrir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valorAposta\":999999999999.99}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.premio").isNumber())
                .andExpect(content().string(containsString("\"premio\":19999999999999.80")));
    }
}
