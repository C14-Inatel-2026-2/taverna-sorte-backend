package com.tavernasorte.controller;

import com.tavernasorte.dto.EnchantedChestResponse;
import com.tavernasorte.exception.InvalidBetException;
import com.tavernasorte.exception.GlobalExceptionHandler;
import com.tavernasorte.model.Symbol;
import com.tavernasorte.model.ChestBetValue;
import com.tavernasorte.service.EnchantedChestService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EnchantedChestControllerTest {

    private static final String ROTA = "/bau/abrir";
    private static final BigDecimal APOSTA = new BigDecimal("10.0");

    @Mock
    private EnchantedChestService bauEncantadoService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new EnchantedChestController(bauEncantadoService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"valorAposta", "valor"})
    void abrirDeveAceitarONomeAtualEOAnteriorDoCampo(String campo) throws Exception {
        EnchantedChestResponse resultado = new EnchantedChestResponse(
                List.of(Symbol.DRAGAO, Symbol.DRAGAO, Symbol.DRAGAO), new BigDecimal("200.00"));
        when(bauEncantadoService.abrir(APOSTA)).thenReturn(resultado);

        mockMvc.perform(post(ROTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"" + campo + "\":10.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.simbolos").value(contains("DRAGAO", "DRAGAO", "DRAGAO")))
                .andExpect(jsonPath("$.premio").isNumber())
                .andExpect(jsonPath("$.premio").value(200.0));

        verify(bauEncantadoService).abrir(APOSTA);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "0.0", "-10.0"})
    void abrirComValorNaoPositivoDeveRetornar400SemChamarOServico(String valor) throws Exception {
        mockMvc.perform(post(ROTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valorAposta\":" + valor + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("O valor da aposta deve ser maior que zero"));

        verifyNoInteractions(bauEncantadoService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"valorAposta\":null}", "{\"valor\":null}"})
    void abrirSemValorDeveRetornar400SemChamarOServico(String corpo) throws Exception {
        mockMvc.perform(post(ROTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("O valor da aposta é obrigatório"));

        verifyNoInteractions(bauEncantadoService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.001", "10.000", "1000000000000.00", "1e308"})
    void abrirComValorForaDosLimitesDeveRetornar400SemChamarOServico(String valor) throws Exception {
        mockMvc.perform(post(ROTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valorAposta\":" + valor + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(ChestBetValue.FORMATO_INVALIDO));

        verifyNoInteractions(bauEncantadoService);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"valorAposta\":\"abc\"}", "{valorAposta:", "",
            "{\"valorAposta\":\"NaN\"}", "{\"valorAposta\":\"Infinity\"}", "{\"valorAposta\":NaN}"
    })
    void abrirComCorpoIlegivelDeveRetornar400SemChamarOServico(String corpo) throws Exception {
        mockMvc.perform(post(ROTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Valor inválido na requisição"));

        verifyNoInteractions(bauEncantadoService);
    }

    @Test
    void abrirQuandoServicoRejeitarApostaDeveRetornar400ComMensagemDoServico() throws Exception {
        when(bauEncantadoService.abrir(APOSTA))
                .thenThrow(new InvalidBetException("O valor da aposta deve ser maior que zero"));

        mockMvc.perform(post(ROTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valorAposta\":10.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("O valor da aposta deve ser maior que zero"));
    }
}
