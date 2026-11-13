package com.tavernasorte.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({PlayerNotFoundException.class, ItemNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyInUse(
            EmailAlreadyInUseException exception
    ) {
        return error(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler({
            InvalidBetException.class, InvalidPlayerException.class, InvalidItemException.class,
            InvalidBalanceException.class, InsufficientBalanceException.class,
            InventoryFullException.class, DanceNotAllowedException.class
    })
    public ResponseEntity<Map<String, String>> handleBusinessError(
            RuntimeException exception
    ) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Dados inválidos");

        return error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, String>> handleInvalidRequest(
            Exception exception
    ) {
        return error(HttpStatus.BAD_REQUEST, "Valor inválido na requisição");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpectedException(
            Exception exception
    ) {
        LOGGER.error("Erro inesperado ao processar a requisição", exception);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor");
    }

    @ExceptionHandler({
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            NoHandlerFoundException.class,
            NoResourceFoundException.class
    })
    public ResponseEntity<Map<String, String>> handleHttpError(Exception exception) {
        ErrorResponse response = (ErrorResponse) exception;
        String message = switch (response.getStatusCode().value()) {
            case 404 -> "Recurso não encontrado";
            case 405 -> "Método HTTP não permitido";
            default -> "Tipo de conteúdo não suportado";
        };
        return ResponseEntity.status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(Map.of("error", message));
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }
}
