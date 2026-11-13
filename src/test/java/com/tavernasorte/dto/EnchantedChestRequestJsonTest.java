package com.tavernasorte.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Usa o ObjectMapper configurado pelo Spring Boot a partir do application.yml,
 * garantindo que a aplicação real recusa valores fracionários onde se espera inteiro.
 */
@JsonTest
class EnchantedChestRequestJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReadIntegerBetAmount() throws Exception {
        EnchantedChestRequest request = objectMapper.readValue(
                "{\"valorAposta\":10}", EnchantedChestRequest.class);

        assertEquals(10, request.getBetAmount());
    }

    @ParameterizedTest
    @ValueSource(strings = {"10.5", "10.0", "1e2"})
    void shouldRejectFractionalBetAmountInsteadOfTruncating(String value) {
        assertThrows(MismatchedInputException.class, () -> objectMapper.readValue(
                "{\"valorAposta\":" + value + "}", EnchantedChestRequest.class));
    }
}
