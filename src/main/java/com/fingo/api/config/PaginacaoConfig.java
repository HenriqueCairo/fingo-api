package com.fingo.api.config;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class PaginacaoConfig {

    public static final int TAMANHO_MAXIMO = 50;

    private PaginacaoConfig() {
    }

    public static PageRequest criar(int page, int size) {

        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A página não pode ser negativa."
            );
        }

        if (size < 1 || size > TAMANHO_MAXIMO) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O tamanho da página deve estar entre 1 e 50."
            );
        }

        return PageRequest.of(page, size);
    }
}
