
package com.fingo.api.controller;

import com.fingo.api.security.ApiKeyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/api/admin/api-keys")
public class ApiKeyController {

    private final ApiKeyService service;
    private final String bootstrapToken;

    public ApiKeyController(
            ApiKeyService service,
            @Value("${fingo.bootstrap-token:}")
            String bootstrapToken
    ) {
        this.service = service;
        this.bootstrapToken = bootstrapToken;
    }

    public record CriarChaveRequest(
            @NotBlank
            @Size(min = 3, max = 100)
            String descricao
    ) {}

    @PostMapping
    public ResponseEntity<ApiKeyService.ChaveGerada> criar(
            @RequestHeader(
                    value = "X-Bootstrap-Token",
                    required = false
            ) String token,
            @Valid @RequestBody CriarChaveRequest dados
    ) {
        if (bootstrapToken.isBlank()
                || token == null
                || !MessageDigest.isEqual(
                bootstrapToken.getBytes(
                        StandardCharsets.UTF_8),
                token.getBytes(
                        StandardCharsets.UTF_8))) {

            return ResponseEntity.status(
                    HttpStatus.UNAUTHORIZED
            ).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.gerar(dados.descricao()));
    }
}

