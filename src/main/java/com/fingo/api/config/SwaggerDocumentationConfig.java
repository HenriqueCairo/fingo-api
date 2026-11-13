package com.fingo.api.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class SwaggerDocumentationConfig {

    private record Modulo(
            String tag,
            String singular,
            String plural,
            String descricao
    ) {}

    private static final Map<String, Modulo> MODULOS =
            new LinkedHashMap<>();

    static {
        MODULOS.put("/api/usuarios",
                new Modulo(
                        "01 | Gestão de Usuários",
                        "usuário",
                        "usuários",
                        "Cadastro e gerenciamento de usuários."
                ));

        MODULOS.put("/api/perfis-financeiros",
                new Modulo(
                        "02 | Perfis Financeiros",
                        "perfil financeiro",
                        "perfis financeiros",
                        "Classificação financeira dos usuários."
                ));

        MODULOS.put("/api/trilhas",
                new Modulo(
                        "03 | Trilhas de Aprendizagem",
                        "trilha",
                        "trilhas",
                        "Jornadas de educação financeira."
                ));

        MODULOS.put("/api/aulas",
                new Modulo(
                        "04 | Conteúdo Educacional",
                        "aula",
                        "aulas",
                        "Conteúdos das trilhas."
                ));

        MODULOS.put("/api/conquistas",
                new Modulo(
                        "05 | Gamificação e Conquistas",
                        "conquista",
                        "conquistas",
                        "Pontuação e reconhecimento."
                ));
    }

    @Bean
    public OpenApiCustomizer documentacaoFinGo() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }

            openApi.getPaths().forEach((path, item) -> {
                if (path.equals("/api/status")) {
                    documentarStatus(item);
                    return;
                }

                MODULOS.forEach((base, modulo) -> {
                    if (path.equals(base)
                            || path.startsWith(base + "/")) {
                        documentarRecurso(
                                path, base, item, modulo
                        );
                    }
                });
            });
        };
    }

    private void documentarStatus(PathItem item) {
        item.readOperationsMap().forEach((metodo, operacao) -> {
            operacao.setTags(List.of("06 | Monitoramento"));
            operacao.setSummary("Verificar disponibilidade da API");
            operacao.setDescription(
                    "Verifica se o serviço FinGo está respondendo."
            );

            definirRespostas(operacao,
                    Map.of("200", "API em funcionamento"));
        });
    }

    private void documentarRecurso(
            String path,
            String base,
            PathItem item,
            Modulo modulo
    ) {
        item.readOperationsMap().forEach((metodo, operacao) -> {
            operacao.setTags(List.of(modulo.tag()));

            String sufixo = path.substring(base.length());

            if (sufixo.equals("/pesquisar")) {
                configurar(operacao,
                        "Pesquisar " + modulo.plural(),
                        "Consulta personalizada com filtros e paginação."
                );

                definirRespostas(operacao,
                        Map.of(
                                "200", "Pesquisa realizada",
                                "400", "Parâmetros inválidos"
                        ));

                documentarPaginacao(operacao);
                return;
            }

            if (sufixo.endsWith("/detalhes")) {
                configurar(operacao,
                        "Consultar detalhes HATEOAS",
                        "Retorna o recurso com links de navegação."
                );

                definirRespostas(operacao,
                        Map.of(
                                "200", "Recurso encontrado com links",
                                "404", "Recurso não encontrado"
                        ));
                return;
            }

            if (path.startsWith("/api/usuarios/")
                    && sufixo.endsWith("/conquistas")) {
                configurar(operacao,
                        "Consultar conquistas do usuário",
                        "Retorna as conquistas vinculadas ao usuário."
                );

                definirRespostas(operacao,
                        Map.of(
                                "200", "Conquistas consultadas",
                                "404", "Usuário não encontrado"
                        ));
                return;
            }

            if (path.startsWith("/api/usuarios/")
                    && sufixo.contains("/conquistas/")) {
                configurar(operacao,
                        "Atribuir conquista ao usuário",
                        "Associa uma conquista existente a um usuário."
                );

                definirRespostas(operacao,
                        Map.of(
                                "204", "Conquista atribuída",
                                "404", "Usuário ou conquista não encontrado"
                        ));
                return;
            }

            if (sufixo.isEmpty()) {
                switch (metodo) {
                    case GET -> {
                        configurar(operacao,
                                "Listar " + modulo.plural(),
                                "Retorna uma página de "
                                        + modulo.plural() + "."
                        );

                        definirRespostas(operacao,
                                Map.of(
                                        "200", "Listagem realizada",
                                        "400", "Parâmetros inválidos"
                                ));

                        documentarPaginacao(operacao);
                    }

                    case POST -> {
                        configurar(operacao,
                                "Cadastrar " + modulo.singular(),
                                "Cria um novo registro validado."
                        );

                        Map<String, String> respostas =
                                new LinkedHashMap<>();

                        respostas.put("201", "Recurso criado");
                        respostas.put("400", "Dados inválidos");

                        if (base.equals("/api/usuarios")) {
                            respostas.put(
                                    "409",
                                    "E-mail já cadastrado"
                            );
                        }

                        definirRespostas(operacao, respostas);
                    }

                    default -> {
                    }
                }
                return;
            }

            if (sufixo.equals("/{id}")) {
                switch (metodo) {
                    case GET -> {
                        configurar(operacao,
                                "Consultar " + modulo.singular() + " por ID",
                                "Busca o recurso pelo identificador."
                        );

                        definirRespostas(operacao,
                                Map.of(
                                        "200", "Recurso encontrado",
                                        "404", "Recurso não encontrado"
                                ));
                    }

                    case PUT -> {
                        configurar(operacao,
                                "Atualizar " + modulo.singular(),
                                "Atualiza um recurso existente."
                        );

                        Map<String, String> respostas =
                                new LinkedHashMap<>();

                        respostas.put("200", "Recurso atualizado");
                        respostas.put("400", "Dados inválidos");
                        respostas.put("404", "Recurso não encontrado");

                        if (base.equals("/api/usuarios")) {
                            respostas.put(
                                    "409",
                                    "E-mail já cadastrado"
                            );
                        }

                        definirRespostas(operacao, respostas);
                    }

                    case DELETE -> {
                        configurar(operacao,
                                "Excluir " + modulo.singular(),
                                "Remove o recurso identificado pelo ID."
                        );

                        definirRespostas(operacao,
                                Map.of(
                                        "204", "Recurso excluído",
                                        "404", "Recurso não encontrado"
                                ));
                    }

                    default -> {
                    }
                }
            }
        });
    }

    private void configurar(
            Operation operacao,
            String resumo,
            String descricao
    ) {
        operacao.setSummary(resumo);
        operacao.setDescription(descricao);
    }

    private void definirRespostas(
            Operation operacao,
            Map<String, String> codigos
    ) {
        ApiResponses anteriores = operacao.getResponses();
        ApiResponses novas = new ApiResponses();

        codigos.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entrada -> {
                    String codigo = entrada.getKey();

                    ApiResponse resposta = anteriores == null
                            ? null
                            : anteriores.get(codigo);

                    // Preserva o schema de resposta gerado pelo
                    // Springdoc quando o sucesso muda de 200 para 201.
                    if (resposta == null
                            && codigo.equals("201")
                            && anteriores != null) {
                        resposta = anteriores.get("200");
                    }

                    if (resposta == null) {
                        resposta = new ApiResponse();
                    }

                    resposta.setDescription(entrada.getValue());

                    // 204 nunca deve documentar corpo de resposta.
                    if (codigo.equals("204")) {
                        resposta.setContent(null);
                    }

                    novas.addApiResponse(codigo, resposta);
                });

        operacao.setResponses(novas);
    }

    private void documentarPaginacao(Operation operacao) {
        if (operacao.getParameters() == null) {
            return;
        }

        for (Parameter parametro : operacao.getParameters()) {
            if ("page".equals(parametro.getName())) {
                parametro.setDescription(
                        "Índice da página. Valor mínimo: 0."
                );
                parametro.setExample(0);

                parametro.setSchema(
                        new IntegerSchema()
                                ._default(0)
                                .minimum(java.math.BigDecimal.ZERO)
                );
            }

            if ("size".equals(parametro.getName())) {
                parametro.setDescription(
                        "Registros por página. "
                                + "Mínimo: 1. Máximo: 50."
                );
                parametro.setExample(10);

                parametro.setSchema(
                        new IntegerSchema()
                                ._default(10)
                                .minimum(java.math.BigDecimal.ONE)
                                .maximum(
                                        java.math.BigDecimal.valueOf(50)
                                )
                );
            }
        }
    }
}


