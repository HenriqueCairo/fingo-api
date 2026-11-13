
package com.fingo.api.security;

import com.fingo.api.entity.ApiKey;
import com.fingo.api.repository.ApiKeyRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

@Service
public class ApiKeyService {

    private final ApiKeyRepository repository;
    private final SecureRandom random = new SecureRandom();

    public ApiKeyService(ApiKeyRepository repository) {
        this.repository = repository;
    }

    public record ChaveGerada(
            Long id,
            String descricao,
            String chave
    ) {}

    public ChaveGerada gerar(String descricao) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        String chave = HexFormat.of().formatHex(bytes);

        ApiKey apiKey = new ApiKey();
        apiKey.setDescricao(descricao.trim());
        apiKey.setChaveHash(hash(chave));

        ApiKey salva = repository.save(apiKey);

        return new ChaveGerada(
                salva.getId(),
                salva.getDescricao(),
                chave
        );
    }

    public boolean validar(String chave) {
        if (chave == null || chave.isBlank()) {
            return false;
        }

        return repository.existsByChaveHashAndAtivaTrue(
                hash(chave)
        );
    }

    private String hash(String valor) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] resultado = digest.digest(
                    valor.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(resultado);

        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(
                    "SHA-256 indisponível", ex
            );
        }
    }
}

