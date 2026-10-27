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

    private static final String ROUTE = "/bau/abrir";
    private static final BigDecimal BET_AMOUNT = new BigDecimal("10.0");

    @Mock
    private EnchantedChestService enchantedChestService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new EnchantedChestController(enchantedChestService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"valorAposta", "valor"})
    void shouldAcceptCurrentAndLegacyFieldNames(String field) throws Exception {
        EnchantedChestResponse result = new EnchantedChestResponse(
                List.of(Symbol.DRAGON, Symbol.DRAGON, Symbol.DRAGON), new BigDecimal("200.00"));
        when(enchantedChestService.open(BET_AMOUNT)).thenReturn(result);

        mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"" + field + "\":10.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.simbolos").value(contains("DRAGAO", "DRAGAO", "DRAGAO")))
                .andExpect(jsonPath("$.premio").isNumber())
                .andExpect(jsonPath("$.premio").value(200.0));

        verify(enchantedChestService).open(BET_AMOUNT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "0.0", "-10.0"})
    void shouldRejectNonPositiveAmountWithoutCallingService(String value) throws Exception {
        mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valorAposta\":" + value + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("O valor da aposta deve ser maior que zero"));

        verifyNoInteractions(enchantedChestService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"valorAposta\":null}", "{\"valor\":null}"})
    void shouldRejectMissingAmountWithoutCallingService(String body) throws Exception {
        mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("O valor da aposta é obrigatório"));

        verifyNoInteractions(enchantedChestService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.001", "10.000", "1000000000000.00", "1e308"})
    void shouldRejectOutOfRangeAmountWithoutCallingService(String value) throws Exception {
        mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valorAposta\":" + value + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(ChestBetValue.INVALID_FORMAT));

        verifyNoInteractions(enchantedChestService);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"valorAposta\":\"abc\"}", "{valorAposta:", "",
            "{\"valorAposta\":\"NaN\"}", "{\"valorAposta\":\"Infinity\"}", "{\"valorAposta\":NaN}"
    })
    void shouldRejectUnreadableBodyWithoutCallingService(String body) throws Exception {
        mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Valor inválido na requisição"));

        verifyNoInteractions(enchantedChestService);
    }

    @Test
    void shouldReturnServiceMessageWhenBetIsRejected() throws Exception {
        when(enchantedChestService.open(BET_AMOUNT))
                .thenThrow(new InvalidBetException("O valor da aposta deve ser maior que zero"));

        mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valorAposta\":10.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("O valor da aposta deve ser maior que zero"));
    }
}
