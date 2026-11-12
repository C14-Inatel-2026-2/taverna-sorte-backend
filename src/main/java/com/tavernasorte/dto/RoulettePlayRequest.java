package com.tavernasorte.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.tavernasorte.model.RouletteBetType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.IOException;

public record RoulettePlayRequest(
        @NotNull(message = "O tipo de aposta é obrigatório") RouletteBetType betType,
        @JsonDeserialize(using = IntegerValueDeserializer.class) Integer chosenNumber,
        @NotNull(message = "O valor da aposta é obrigatório")
        @Positive(message = "A aposta deve ser positiva")
        @JsonDeserialize(using = IntegerValueDeserializer.class) Integer betAmount
) {
    // Impede que o Jackson converta silenciosamente 1.5 em 1, apenas neste DTO.
    public static class IntegerValueDeserializer extends StdDeserializer<Integer> {
        public IntegerValueDeserializer() {
            super(Integer.class);
        }

        @Override
        public Integer deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            if (!parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                return (Integer) context.handleUnexpectedToken(Integer.class, parser);
            }
            return parser.getIntValue();
        }
    }
}
