
package com.fingo.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ReadListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IdempotencyFilter extends OncePerRequestFilter {

    private record Resultado(
            String assinatura,
            int status,
            String contentType,
            byte[] corpo
    ) {}

    private final Map<String, Resultado> resultados =
            new ConcurrentHashMap<>();

    private final Map<String, Object> bloqueios =
            new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"POST".equalsIgnoreCase(request.getMethod())
                || !request.getServletPath().startsWith("/api/")
                || request.getServletPath()
                .equals("/api/admin/api-keys");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String chave = request.getHeader("Idempotency-Key");

        if (chave == null || chave.isBlank()) {
            chain.doFilter(request, response);
            return;
        }

        if (chave.length() > 128) {
            response.sendError(400, "Idempotency-Key muito longa");
            return;
        }

        byte[] corpo = request.getInputStream().readAllBytes();

        String identidade = request.getHeader("X-API-Key");

        String identificador = assinatura(
                (identidade == null ? "" : identidade)
                        .getBytes(StandardCharsets.UTF_8)
        ) + ":" + request.getServletPath() + ":" + chave;

        String assinaturaRequisicao = assinatura(corpo);

        Object bloqueio = bloqueios.computeIfAbsent(
                identificador, k -> new Object()
        );

        synchronized (bloqueio) {
            Resultado anterior = resultados.get(identificador);

            if (anterior != null) {
                if (!anterior.assinatura()
                        .equals(assinaturaRequisicao)) {
                    response.setStatus(409);
                    response.setContentType("application/json");
                    response.getWriter().write(
                            "{\"status\":409,"
                                    + "\"mensagem\":\"Idempotency-Key reutilizada "
                                    + "com conteúdo diferente.\"}"
                    );
                    return;
                }

                response.setStatus(anterior.status());

                if (anterior.contentType() != null) {
                    response.setContentType(anterior.contentType());
                }

                response.setHeader("Idempotency-Replayed", "true");
                response.getOutputStream().write(anterior.corpo());
                return;
            }

            HttpServletRequestWrapper requestWrapper =
                    new HttpServletRequestWrapper(request) {

                        @Override
                        public ServletInputStream getInputStream() {
                            ByteArrayInputStream entrada =
                                    new ByteArrayInputStream(corpo);

                            return new ServletInputStream() {
                                @Override
                                public int read() {
                                    return entrada.read();
                                }

                                @Override
                                public boolean isFinished() {
                                    return entrada.available() == 0;
                                }

                                @Override
                                public boolean isReady() {
                                    return true;
                                }

                                @Override
                                public void setReadListener(
                                        ReadListener listener) {
                                }
                            };
                        }

                        @Override
                        public BufferedReader getReader() {
                            return new BufferedReader(
                                    new InputStreamReader(
                                            getInputStream(),
                                            StandardCharsets.UTF_8
                                    )
                            );
                        }
                    };

            ContentCachingResponseWrapper resposta =
                    new ContentCachingResponseWrapper(response);

            try {
                chain.doFilter(requestWrapper, resposta);

                byte[] respostaBytes =
                        resposta.getContentAsByteArray();

                if (resposta.getStatus() >= 200
                        && resposta.getStatus() < 300) {

                    resultados.put(identificador,
                            new Resultado(
                                    assinaturaRequisicao,
                                    resposta.getStatus(),
                                    resposta.getContentType(),
                                    Arrays.copyOf(
                                            respostaBytes,
                                            respostaBytes.length
                                    )
                            )
                    );
                }
            } finally {
                resposta.copyBodyToResponse();
            }
        }
    }

    private String assinatura(byte[] dados) {
        try {
            return java.util.HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256")
                            .digest(dados)
            );
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}

