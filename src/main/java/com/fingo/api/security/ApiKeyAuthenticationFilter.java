
package com.fingo.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final ApiKeyService service;

    public ApiKeyAuthenticationFilter(ApiKeyService service) {
        this.service = service;
    }

    @Override
    protected boolean shouldNotFilter(

            HttpServletRequest request
    )


    {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String path = request.getServletPath();

        // Documentação pública e criação administrativa
        // protegida separadamente pelo token bootstrap.
        return !path.startsWith("/api/")
                || path.equals("/api/status")
                || path.equals("/api/admin/api-keys");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String chave = request.getHeader("X-API-Key");

        if (!service.validar(chave)) {
            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            response.setContentType(
                    "application/json;charset=UTF-8"
            );

            response.getWriter().write(
                    "{\"status\":401,"
                            + "\"erro\":\"Unauthorized\","
                            + "\"mensagem\":\"API Key ausente ou inválida.\"}"
            );
            return;
        }

        chain.doFilter(request, response);
    }
}



