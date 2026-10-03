package com.tavernasorte.service;

import com.tavernasorte.dto.CreateUserRequest;
import com.tavernasorte.dto.UserResponse;
import com.tavernasorte.entity.User;
import com.tavernasorte.exception.EmailAlreadyInUseException;
import com.tavernasorte.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test

    void deveCriarUsuarioComSucesso() {
        String email = "luiz@example.com";
        String password = "senha123";

        CreateUserRequest request =
                new CreateUserRequest("Luiz", "  LUIZ@EXAMPLE.COM  ", password);

        User savedUser =
                new User("Luiz", email, "senha-hasheada");

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("senha-hasheada");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.create(request);

        assertEquals(email, response.getEmail());
    }

    @Test
    void naoDeveCriarUsuarioQuandoEmailJaEstiverEmUso() {
        String email = "luiz@example.com";

        CreateUserRequest request =
                new CreateUserRequest("Luiz", email, "senha123");

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(
                EmailAlreadyInUseException.class,
                () -> userService.create(request)
        );
    }
}
