
package com.fingo.api.controller;

import com.fingo.api.entity.Usuario;
import com.fingo.api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/versoes/usuarios")
public class UsuarioVersionController {

    private final UsuarioRepository repository;

    public UsuarioVersionController(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Operation(
            summary = "Consultar usuário - Versão 1",
            description = """
        Consulta os dados básicos de um usuário.

        Versão 1 da API FinGo.

        Retorna ID, nome e e-mail.
        """,
            parameters = {
                    @Parameter(
                            name = "X-API-Version",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Versão da API. Utilize 1.",
                            schema = @Schema(
                                    type = "string",
                                    allowableValues = {"1"},
                                    defaultValue = "1"
                            )
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
            }
    )

    @GetMapping(value = "/{id}", headers = "X-API-Version=1")
    public Map<String, Object> consultarV1(@PathVariable Long id) {
        Usuario usuario = encontrar(id);

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("id", usuario.getId());
        resposta.put("nome", usuario.getNome());
        resposta.put("email", usuario.getEmail());

        return resposta;
    }

    @Operation(
            summary = "Consultar usuário - Versão 2",
            description = """
        Consulta os dados expandidos de um usuário.

        Versão 2 da API FinGo.

        Além de ID, nome e e-mail, retorna a versão
        e a quantidade de conquistas do usuário.
        """,
            parameters = {
                    @Parameter(
                            name = "X-API-Version",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Versão da API. Utilize 2.",
                            schema = @Schema(
                                    type = "string",
                                    allowableValues = {"2"},
                                    defaultValue = "2"
                            )
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
            }
    )

    @GetMapping(value = "/{id}", headers = "X-API-Version=2")
    public Map<String, Object> consultarV2(@PathVariable Long id) {
        Usuario usuario = repository.findWithConquistasById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado"
                ));

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("id", usuario.getId());
        resposta.put("nome", usuario.getNome());
        resposta.put("email", usuario.getEmail());
        resposta.put("versao", "2.0");
        resposta.put("totalConquistas",
                usuario.getConquistas().size());

        return resposta;
    }

    private Usuario encontrar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado"
                ));
    }
}

