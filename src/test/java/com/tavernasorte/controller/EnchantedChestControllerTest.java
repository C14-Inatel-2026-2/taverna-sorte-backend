package com.tavernasorte.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.tavernasorte.dto.EnchantedChestResponse;
import com.tavernasorte.exception.GlobalExceptionHandler;
import com.tavernasorte.exception.InvalidBetException;
import com.tavernasorte.exception.PlayerNotFoundException;
import com.tavernasorte.model.ChestBetValue;
import com.tavernasorte.model.Symbol;
import com.tavernasorte.service.EnchantedChestService;
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
    private static final Long PLAYER_ID = 1L;
    private static final int BET_AMOUNT = 10;

    @Mock
    private EnchantedChestService enchantedChestService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Mesma regra do application.yml (spring.jackson.deserialization.accept-float-as-int: false),
        // que o standaloneSetup não carrega.
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(
                Jackson2ObjectMapperBuilder.json()
                        .featuresToDisable(DeserializationFeature.ACCEPT_FLOAT_AS_INT)
                        .build());

        mockMvc = MockMvcBuilders.standaloneSetup(new EnchantedChestController(enchantedChestService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(jsonConverter)
                .build();
    }

    @Test
    void shouldOpenChestForPlayer() throws Exception {
        EnchantedChestResponse result = new EnchantedChestResponse(
                List.of(Symbol.DRAGON, Symbol.DRAGON, Symbol.DRAGON), 200, 1190);
        when(enchantedChestService.open(PLAYER_ID, BET_AMOUNT)).thenReturn(result);

        mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"jogadorId":1,"valorAposta":10}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.simbolos").value(contains("DRAGAO", "DRAGAO", "DRAGAO")))
                .andExpect(jsonPath("$.premio").value(200))
                .andExpect(jsonPath("$.saldo").value(1190));

        verify(enchantedChestService).open(PLAYER_ID, BET_AMOUNT);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            """
            {"valorAposta":10}
            """,
            """
            {"jogadorId":null,"valorAposta":10}
            """
    })
    void shouldRejectMissingPlayerWithoutCallingService(String body) throws Exception {
        mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(EnchantedChestService.MISSING_PLAYER));

        verifyNoInteractions(enchantedChestService);
    }

    @Test
    void shouldReturnNotFoundForUnknownPlayer() throws Exception {
        when(enchantedChestService.open(99L, BET_AMOUNT))
                .thenThrow(new PlayerNotFoundException());

        mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"jogadorId":99,"valorAposta":10}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Jogador não encontrado"));
    }

    @Test
    void shouldReturnBadRequestForInsufficientBalance() throws Exception {
        when(enchantedChestService.open(PLAYER_ID, BET_AMOUNT))
                .thenThrow(new InvalidBetException(EnchantedChestService.INSUFFICIENT_BALANCE));

        mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"jogadorId":1,"valorAposta":10}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(EnchantedChestService.INSUFFICIENT_BALANCE));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "-10"})
    void shouldRejectNonPositiveAmountWithoutCallingService(String value) throws Exception {
        mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"jogadorId":1,"valorAposta":%s}
                                """.formatted(value)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("O valor da aposta deve ser maior que zero"));

        verifyNoInteractions(enchantedChestService);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            """
            {"jogadorId":1}
            """,
            """
            {"jogadorId":1,"valorAposta":null}
            """
    })
    void shouldRejectMissingAmountWithoutCallingService(String body) throws Exception {
        mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("O valor da aposta é obrigatório"));

        verifyNoInteractions(enchantedChestService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"10001", "100000", "2147483647"})
    void shouldRejectAmountAboveMaximumWithoutCallingService(String value) throws Exception {
        mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"jogadorId":1,"valorAposta":%s}
                                """.formatted(value)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(ChestBetValue.INVALID_MAXIMUM_VALUE));

        verifyNoInteractions(enchantedChestService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"10.5", "10.0", "0.0", "-10.0", "0.001", "1e308", "2147483648", "1000000000000"})
    void shouldRejectNonIntegerAmountWithoutCallingService(String value) throws Exception {
        mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"jogadorId":1,"valorAposta":%s}
                                """.formatted(value)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Valor inválido na requisição"));

        verifyNoInteractions(enchantedChestService);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            """
            {"jogadorId":1.5,"valorAposta":10}
            """,
            """
            {"jogadorId":"abc","valorAposta":10}
            """
    })
    void shouldRejectNonIntegerPlayerIdWithoutCallingService(String body) throws Exception {
        mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Valor inválido na requisição"));

        verifyNoInteractions(enchantedChestService);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            """
            {"jogadorId":1,"valorAposta":"abc"}
            """,
            "{valorAposta:",
            "",
            """
            {"jogadorId":1,"valorAposta":"NaN"}
            """,
            """
            {"jogadorId":1,"valorAposta":"Infinity"}
            """,
            """
            {"jogadorId":1,"valorAposta":NaN}
            """
    })
    void shouldRejectUnreadableBodyWithoutCallingService(String body) throws Exception {
        mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Valor inválido na requisição"));

        verifyNoInteractions(enchantedChestService);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            """
            {"jogadorId":1,"valorApostaa":10}
            """,
            """
            {"jogadorId":1,"valor":10}
            """,
            """
            {"jogadorId":1,"valorAposta":10,"extra":true}
            """,
            """
            {"playerId":1,"valorAposta":10}
            """
    })
    void shouldRejectUnknownFieldWithoutCallingService(String body) throws Exception {
        mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Valor inválido na requisição"));

        verifyNoInteractions(enchantedChestService);
    }
}
