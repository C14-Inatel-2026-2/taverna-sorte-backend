package com.tavernasorte.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tavernasorte.model.BetType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiceJsonContractTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldReadExistingRequestFieldsWithEnglishAccessors() throws Exception {
        DicePlayRequest request = objectMapper.readValue(
                "{\"tipoAposta\":\"LOW\",\"valorAposta\":10.0}", DicePlayRequest.class);

        assertEquals(BetType.LOW, request.getBetType());
        assertEquals(10.0, request.getBetAmount());
    }

    @Test
    void shouldKeepExistingResponseFieldNames() throws Exception {
        DicePlayResponse response = new DicePlayResponse(2, 3, 5, BetType.LOW, true, 20.0);

        assertEquals(objectMapper.readTree(
                "{\"dado1\":2,\"dado2\":3,\"soma\":5,\"tipoAposta\":\"LOW\",\"venceu\":true,\"premio\":20.0}"),
                objectMapper.valueToTree(response));
    }
}
