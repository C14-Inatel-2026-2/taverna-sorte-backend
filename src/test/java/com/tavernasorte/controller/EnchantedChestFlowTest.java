package com.tavernasorte.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.tavernasorte.database.entity.Item;
import com.tavernasorte.database.entity.Player;
import com.tavernasorte.database.repository.PlayerRepository;
import com.tavernasorte.exception.GlobalExceptionHandler;
import com.tavernasorte.model.ChestBetValue;
import com.tavernasorte.model.Symbol;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    private static final Long PLAYER_ID = 1L;

    @Mock
    private SymbolPicker symbolPicker;

    @Mock
    private PlayerRepository playerRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Mesma regra do application.yml (spring.jackson.deserialization.accept-float-as-int: false),
        // que o standaloneSetup não carrega.
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(
                Jackson2ObjectMapperBuilder.json()
                        .featuresToDisable(DeserializationFeature.ACCEPT_FLOAT_AS_INT)
                        .build());

        EnchantedChestService service = new EnchantedChestService(symbolPicker, playerRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(new EnchantedChestController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(jsonConverter)
                .build();
    }

    @Test
    void shouldPlayWithItemAndUpdateBalance() throws Exception {
        Player player = givenPlayerWithBalance(1000);
        Item item = new Item();
        item.setMultiplier(2);
        ReflectionTestUtils.setField(item, "id", 3L);
        player.getInventory().addItem(item);
        when(symbolPicker.pick()).thenReturn(Symbol.COIN);

        mockMvc.perform(post("/bau/abrir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"jogadorId":1,"valorAposta":10,"itemId":3}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.simbolos").value(contains("MOEDA", "MOEDA", "MOEDA")))
                .andExpect(jsonPath("$.premio").value(60))
                .andExpect(jsonPath("$.saldo").value(1050));

        verify(symbolPicker, times(3)).pick();
        assertTrue(player.getInventory().getItems().isEmpty());
    }

    @Test
    void shouldReturnOkWithZeroPrizeWhenPlayerLoses() throws Exception {
        Player player = givenPlayerWithBalance(1000);
        when(symbolPicker.pick()).thenReturn(Symbol.COIN, Symbol.POTION, Symbol.SWORD);

        mockMvc.perform(post("/bau/abrir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"jogadorId":1,"valorAposta":10}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.premio").value(0))
                .andExpect(jsonPath("$.saldo").value(990));

        assertEquals(990, player.getBalance());
    }

    @Test
    void shouldRejectBetAboveBalanceBeforePickingSymbols() throws Exception {
        Player player = givenPlayerWithBalance(5);

        mockMvc.perform(post("/bau/abrir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"jogadorId":1,"valorAposta":10}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(EnchantedChestService.INSUFFICIENT_BALANCE));

        assertEquals(5, player.getBalance());
        verifyNoInteractions(symbolPicker);
    }

    @Test
    void shouldReturnNotFoundForUnknownPlayer() throws Exception {
        when(playerRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/bau/abrir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"jogadorId":99,"valorAposta":10}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Jogador não encontrado"));

        verifyNoInteractions(symbolPicker);
    }

    @Test
    void shouldRejectItemOutsideInventoryBeforePickingSymbols() throws Exception {
        givenPlayerWithBalance(1000);

        mockMvc.perform(post("/bau/abrir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"jogadorId":1,"valorAposta":10,"itemId":3}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(EnchantedChestService.ITEM_NOT_IN_INVENTORY));

        verifyNoInteractions(symbolPicker);
    }

    @Test
    void shouldRejectFractionalBetBeforePickingSymbols() throws Exception {
        mockMvc.perform(post("/bau/abrir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"jogadorId":1,"valorAposta":10.5}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Valor inválido na requisição"));

        verifyNoInteractions(symbolPicker, playerRepository);
    }

    @Test
    void shouldReturnMaximumPrizeAsIntegerJsonNumber() throws Exception {
        givenPlayerWithBalance(ChestBetValue.MAXIMUM);
        when(symbolPicker.pick()).thenReturn(Symbol.DRAGON);

        mockMvc.perform(post("/bau/abrir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"jogadorId":1,"valorAposta":10000}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"premio\":200000,")))
                .andExpect(jsonPath("$.saldo").value(200000));
    }

    @Test
    void shouldRejectBetAboveMaximumBeforePickingSymbols() throws Exception {
        mockMvc.perform(post("/bau/abrir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"jogadorId":1,"valorAposta":10001}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(ChestBetValue.INVALID_MAXIMUM_VALUE));

        verifyNoInteractions(symbolPicker, playerRepository);
    }

    private Player givenPlayerWithBalance(int balance) {
        Player player = new Player();
        player.addBalance(balance);
        when(playerRepository.findById(PLAYER_ID)).thenReturn(Optional.of(player));
        return player;
    }
}
