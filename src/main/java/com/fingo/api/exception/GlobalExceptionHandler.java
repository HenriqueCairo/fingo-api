
package com.fingo.api.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> resposta(
            HttpStatus status,
            String mensagem,
            String caminho,
            Object detalhes) {

        Map<String, Object> corpo = new LinkedHashMap<>();

        corpo.put("timestamp", LocalDateTime.now().toString());
        corpo.put("status", status.value());
        corpo.put("erro", status.getReasonPhrase());
        corpo.put("mensagem", mensagem);
        corpo.put("caminho", caminho);

        if (detalhes != null) {
            corpo.put("detalhes", detalhes);
        }

        return ResponseEntity.status(status).body(corpo);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarValidacao(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> campos = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(erro ->
                campos.put(
                        erro.getField(),
                        erro.getDefaultMessage()
                )
        );

        return resposta(
                HttpStatus.BAD_REQUEST,
                "Os dados enviados não passaram na validação.",
                request.getRequestURI(),
                campos
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> tratarJsonInvalido(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return resposta(
                HttpStatus.BAD_REQUEST,
                "JSON inválido ou valor incompatível com o campo.",
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> tratarConflito(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        return resposta(
                HttpStatus.CONFLICT,
                "Operação não permitida por conflito com os dados existentes.",
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> tratarStatus(
            ResponseStatusException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.resolve(
                ex.getStatusCode().value()
        );

        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return resposta(
                status,
                ex.getReason() != null
                        ? ex.getReason()
                        : "Não foi possível concluir a operação.",
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> tratarErroInterno(
            Exception ex,
            HttpServletRequest request) {

        return resposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno na API FinGo.",
                request.getRequestURI(),
                null
        );
    }
}
