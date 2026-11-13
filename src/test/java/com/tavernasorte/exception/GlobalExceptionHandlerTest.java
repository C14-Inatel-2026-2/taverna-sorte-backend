package com.tavernasorte.exception;

import com.tavernasorte.controller.PlayerController;
import com.tavernasorte.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private PlayerService players;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new PlayerController(players))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @ParameterizedTest
    @MethodSource("businessErrors")
    void shouldTranslateCustomExceptionsToStandardHttpErrors(RuntimeException exception, int expectedStatus)
            throws Exception {
        when(players.findById(1L)).thenThrow(exception);

        mvc.perform(get("/players/1"))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.error").value(exception.getMessage()));
    }

    private static Stream<Arguments> businessErrors() {
        return Stream.of(
                Arguments.of(new PlayerNotFoundException(), 404),
                Arguments.of(new ItemNotFoundException(), 404),
                Arguments.of(new EmailAlreadyInUseException(), 409),
                Arguments.of(new InvalidBetException("Aposta inválida"), 400),
                Arguments.of(new InvalidPlayerException("O username é obrigatório"), 400),
                Arguments.of(new InvalidItemException("O item é obrigatório"), 400),
                Arguments.of(new InvalidBalanceException("Crédito inválido"), 400),
                Arguments.of(new InsufficientBalanceException("Saldo insuficiente para a compra"), 400),
                Arguments.of(new InventoryFullException(), 400),
                Arguments.of(new DanceNotAllowedException(), 400)
        );
    }
}
