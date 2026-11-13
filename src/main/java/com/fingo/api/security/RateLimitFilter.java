
package com.fingo.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitFilter extends OncePerRequestFilter {

    private static final int CAPACIDADE = 30;
    private static final double REPOSICAO_POR_SEGUNDO = 0.5;

    private final ConcurrentHashMap<String, Bucket> buckets =
            new ConcurrentHashMap<>();

    private static class Bucket {
        double tokens = CAPACIDADE;
        long ultimaAtualizacao = System.nanoTime();

        synchronized boolean consumir() {
            long agora = System.nanoTime();

            double segundos =
                    (agora - ultimaAtualizacao) / 1_000_000_000.0;

            tokens = Math.min(
                    CAPACIDADE,
                    tokens + segundos * REPOSICAO_POR_SEGUNDO
            );

            ultimaAtualizacao = agora;

            if (tokens < 1) {
                return false;
            }

            tokens -= 1;
            return true;
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }


        String path = request.getServletPath();

        return !path.startsWith("/api/")
                || path.equals("/api/status");
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String chave = request.getHeader("X-API-Key");

        // Chaves ausentes/inválidas serão rejeitadas
        // pelo filtro de autenticação.
        if (chave == null || chave.isBlank()) {
            chain.doFilter(request, response);
            return;
        }

        Bucket bucket = buckets.computeIfAbsent(
                chave, k -> new Bucket()
        );

        if (!bucket.consumir()) {
            response.setStatus(429);
            response.setHeader("Retry-After", "2");
            response.setContentType(
                    "application/json;charset=UTF-8"
            );

            response.getWriter().write(
                    "{\"status\":429,"
                            + "\"erro\":\"Too Many Requests\","
                            + "\"mensagem\":\"Limite de requisições excedido.\"}"
            );
            return;
        }

        chain.doFilter(request, response);
    }
}

