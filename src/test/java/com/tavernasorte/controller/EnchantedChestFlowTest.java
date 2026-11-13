package com.tavernasorte.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.tavernasorte.exception.GlobalExceptionHandler;
import com.tavernasorte.model.Symbol;
import com.tavernasorte.model.ChestBetValue;
import com.tavernasorte.service.EnchantedChestService;
import com.tavernasorte.service.SymbolPicker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
    private SymbolPicker symbolPicker;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Mesma regra do application.yml (spring.jackson.deserialization.accept-float-as-int: false),
        // que o standaloneSetup não carrega.
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(
                Jackson2ObjectMapperBuilder.json()
                        .featuresToDisable(DeserializationFeature.ACCEPT_FLOAT_AS_INT)
                        .build());

        EnchantedChestService service = new EnchantedChestService(symbolPicker);
        mockMvc = MockMvcBuilders.standaloneSetup(new EnchantedChestController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(jsonConverter)
                .build();
    }

    @Test
    void shouldPayPrizeInWholeCoins() throws Exception {
        when(symbolPicker.pick()).thenReturn(Symbol.COIN);

        mockMvc.perform(post("/bau/abrir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"valorAposta":10}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.simbolos").value(contains("MOEDA", "MOEDA", "MOEDA")))
                .andExpect(jsonPath("$.premio").value(30));

        verify(symbolPicker, times(3)).pick();
    }

    @Test
    void shouldRejectFractionalBetBeforePickingSymbols() throws Exception {
        mockMvc.perform(post("/bau/abrir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"valorAposta":10.5}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Valor inválido na requisição"));

        verifyNoInteractions(symbolPicker);
    }

    @Test
    void shouldReturnMaximumPrizeAsIntegerJsonNumber() throws Exception {
        when(symbolPicker.pick()).thenReturn(Symbol.DRAGON);

        mockMvc.perform(post("/bau/abrir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"valorAposta":10000}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"premio\":200000}")));
    }

    @Test
    void shouldRejectBetAboveMaximumBeforePickingSymbols() throws Exception {
        mockMvc.perform(post("/bau/abrir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"valorAposta":10001}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(ChestBetValue.INVALID_MAXIMUM_VALUE));

        verifyNoInteractions(symbolPicker);
    }
}
