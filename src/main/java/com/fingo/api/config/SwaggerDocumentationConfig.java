
package com.fingo.api.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class SwaggerDocumentationConfig {

    private record Modulo(
            String tag,
            String singular,
            String plural,
            String descricao
    ) {}

    private static final Map<String, Modulo> MODULOS = new LinkedHashMap<>();

    static {
        MODULOS.put("/api/usuarios",
                new Modulo("01 | Gestão de Usuários",
                        "usuário", "usuários",
                        "Cadastro e gerenciamento de usuários da plataforma."));

        MODULOS.put("/api/perfis-financeiros",
                new Modulo("02 | Perfis Financeiros",
                        "perfil financeiro", "perfis financeiros",
                        "Classificação financeira e vínculo com usuários."));

        MODULOS.put("/api/trilhas",
                new Modulo("03 | Trilhas de Aprendizagem",
                        "trilha", "trilhas",
                        "Jornadas estruturadas de educação financeira."));

        MODULOS.put("/api/aulas",
                new Modulo("04 | Conteúdo Educacional",
                        "aula", "aulas",
                        "Conteúdos vinculados às trilhas de aprendizagem."));

        MODULOS.put("/api/conquistas",
                new Modulo("05 | Gamificação e Conquistas",
                        "conquista", "conquistas",
                        "Reconhecimento e pontuação do progresso educacional."));
    }

    @Bean
    public OpenApiCustomizer documentacaoFinGo() {
        return openApi -> {
            if (openApi.getPaths() == null) return;

            openApi.getPaths().forEach((path, item) -> {
                if (path.equals("/api/status")) {
                    documentarStatus(item);
                    return;
                }

                MODULOS.forEach((base, modulo) -> {
                    if (path.equals(base) ||
                            path.startsWith(base + "/")) {
                        documentarRecurso(path, base, item, modulo);
                    }
                });
            });
        };
    }

    private void documentarStatus(PathItem item) {
        item.readOperationsMap().forEach((metodo, operacao) -> {
            operacao.setTags(java.util.List.of("06 | Monitoramento"));
            operacao.setSummary("Verificar disponibilidade da API");
            operacao.setDescription(
                    "Verifica se o serviço FinGo está respondendo.");
            resposta(operacao, "200", "API em funcionamento");
        });
    }

    private void documentarRecurso(
            String path,
            String base,
            PathItem item,
            Modulo modulo) {

        item.readOperationsMap().forEach((metodo, operacao) -> {
            operacao.setTags(java.util.List.of(modulo.tag()));

            String sufixo = path.substring(base.length());

            if (sufixo.equals("/pesquisar")) {
                configurar(operacao,
                        "Pesquisar " + modulo.plural(),
                        "Executa uma consulta personalizada com filtros e paginação.",
                        "200", "Pesquisa realizada com sucesso");
                documentarPaginacao(operacao);
                return;
            }

            if (sufixo.endsWith("/detalhes")) {
                configurar(operacao,
                        "Consultar detalhes HATEOAS de " + modulo.singular(),
                        "Retorna o recurso com links de navegação no campo _links.",
                        "200", "Recurso e links retornados");
                resposta(operacao, "404", "Recurso não encontrado");
                return;
            }

            if (path.startsWith("/api/usuarios/")
                    && sufixo.endsWith("/conquistas")) {
                configurar(operacao,
                        "Consultar conquistas do usuário",
                        "Lista as conquistas vinculadas ao usuário informado.",
                        "200", "Conquistas consultadas");
                resposta(operacao, "404", "Usuário não encontrado");
                return;
            }

            if (path.startsWith("/api/usuarios/")
                    && sufixo.contains("/conquistas/")) {
                configurar(operacao,
                        "Atribuir conquista ao usuário",
                        "Associa uma conquista existente a um usuário existente.",
                        "204", "Conquista atribuída");
                resposta(operacao, "404",
                        "Usuário ou conquista não encontrado");
                return;
            }

            if (sufixo.isEmpty()) {
                switch (metodo) {
                    case GET -> {
                        configurar(operacao,
                                "Listar " + modulo.plural(),
                                "Retorna uma página de " + modulo.plural()
                                        + " cadastrados na plataforma.",
                                "200", "Listagem realizada");
                        documentarPaginacao(operacao);
                    }
                    case POST -> {
                        configurar(operacao,
                                "Cadastrar " + modulo.singular(),
                                "Cria um novo registro de " + modulo.singular()
                                        + ". Os campos devem atender às validações.",
                                "201", "Recurso criado");
                        resposta(operacao, "400",
                                "Dados inválidos ou campos obrigatórios ausentes");
                    }
                    default -> {}
                }
                return;
            }

            if (sufixo.equals("/{id}")) {
                switch (metodo) {
                    case GET -> configurar(operacao,
                            "Consultar " + modulo.singular() + " por ID",
                            "Busca o recurso identificado pelo ID informado.",
                            "200", "Recurso encontrado");

                    case PUT -> {
                        configurar(operacao,
                                "Atualizar " + modulo.singular(),
                                "Atualiza os dados do recurso existente.",
                                "200", "Recurso atualizado");
                        resposta(operacao, "400", "Dados inválidos");
                    }

                    case DELETE -> configurar(operacao,
                            "Excluir " + modulo.singular(),
                            "Remove o recurso identificado pelo ID.",
                            "204", "Recurso excluído");

                    default -> {}
                }

                resposta(operacao, "404", "Recurso não encontrado");
            }
        });
    }

    private void configurar(
            Operation operacao,
            String resumo,
            String descricao,
            String codigo,
            String respostaDescricao) {

        operacao.setSummary(resumo);
        operacao.setDescription(descricao);

        resposta(operacao, codigo, respostaDescricao);
    }

    private void resposta(
            Operation operacao,
            String codigo,
            String descricao) {

        if (operacao.getResponses() == null) {
            operacao.setResponses(
                    new io.swagger.v3.oas.models.responses.ApiResponses());
        }

        ApiResponse existente = operacao.getResponses().get(codigo);

        if (existente == null) {
            operacao.getResponses().addApiResponse(
                    codigo, new ApiResponse().description(descricao));
        } else {
            existente.setDescription(descricao);
        }
    }

    private void documentarPaginacao(Operation operacao) {
        if (operacao.getParameters() == null) return;

        for (Parameter parametro : operacao.getParameters()) {
            if ("page".equals(parametro.getName())) {
                parametro.setDescription(
                        "Índice da página, começando em zero.");
                parametro.setExample(0);
            }

            if ("size".equals(parametro.getName())) {
                parametro.setDescription(
                        "Quantidade de registros por página.");
                parametro.setExample(10);
            }
        }
    }
}
