package com.tavernasorte.controller;

import com.tavernasorte.dto.ResultadoBau;
import com.tavernasorte.exception.ApostaInvalidaException;
import com.tavernasorte.exception.GlobalExceptionHandler;
import com.tavernasorte.model.jogo.bau.Simbolo;
import com.tavernasorte.service.jogo.BauEncantadoService;
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

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BauEncantadoControllerTest {

    private static final String ROTA = "/bau/abrir";

    @Mock
    private BauEncantadoService bauEncantadoService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new BauEncantadoController(bauEncantadoService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void abrirComApostaValidaDeveRetornar200ComSimbolosEPremio() throws Exception {
        ResultadoBau resultado = new ResultadoBau(List.of(Simbolo.DRAGAO, Simbolo.DRAGAO, Simbolo.DRAGAO), 200.0);
        when(bauEncantadoService.abrir(10.0)).thenReturn(resultado);

        mockMvc.perform(post(ROTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":10.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.simbolos").value(contains("DRAGAO", "DRAGAO", "DRAGAO")))
                .andExpect(jsonPath("$.premio").value(200.0));

        verify(bauEncantadoService).abrir(10.0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "0.0", "-10.0"})
    void abrirComValorNaoPositivoDeveRetornar400SemChamarOServico(String valor) throws Exception {
        mockMvc.perform(post(ROTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":" + valor + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("O valor da aposta deve ser maior que zero"));

        verifyNoInteractions(bauEncantadoService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"valor\":null}"})
    void abrirSemValorDeveRetornar400SemChamarOServico(String corpo) throws Exception {
        mockMvc.perform(post(ROTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("O valor da aposta é obrigatório"));

        verifyNoInteractions(bauEncantadoService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{\"valor\":\"abc\"}", "{valor:", ""})
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
        when(bauEncantadoService.abrir(10.0))
                .thenThrow(new ApostaInvalidaException("O valor da aposta deve ser maior que zero"));

        mockMvc.perform(post(ROTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":10.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("O valor da aposta deve ser maior que zero"));
    }
}
