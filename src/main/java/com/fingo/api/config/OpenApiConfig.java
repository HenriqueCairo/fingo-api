
package com.fingo.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private final SwaggerDocumentationConfig swaggerDocumentation;

    public OpenApiConfig(
            SwaggerDocumentationConfig swaggerDocumentation
    ) {
        this.swaggerDocumentation = swaggerDocumentation;
    }

    // =========================================================
    // INFORMAÇÕES GERAIS DA API
    // =========================================================

    @Bean
    public OpenAPI fingoOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title(
                                "FinGo API | Plataforma de Educação Financeira Gamificada"
                        )
                        .version("1.0.0")
                        .description("""
                                ## Bem-vindo à FinGo API

                                A FinGo é uma plataforma de educação
                                financeira gamificada, desenvolvida
                                com Java 21 e Spring Boot.

                                ### Objetivo

                                Promover educação financeira por meio
                                de trilhas de aprendizagem, aulas,
                                perfis financeiros e conquistas.

                                ### Módulos da plataforma

                                - **Usuários:** cadastro e gerenciamento.
                                - **Perfis financeiros:** classificação.
                                - **Trilhas:** jornadas de aprendizagem.
                                - **Aulas:** conteúdos educacionais.
                                - **Conquistas:** pontuação e gamificação.

                                ### Tecnologias

                                Java 21, Spring Boot, Spring Data JPA,
                                Hibernate, MySQL, Bean Validation,
                                Spring HATEOAS e OpenAPI 3.

                                ### Convenções REST

                                - GET: consulta de recursos.
                                - POST: criação de recursos.
                                - PUT: atualização de recursos.
                                - DELETE: exclusão de recursos.
                                - Paginação nas listagens.
                                - Respostas em JSON.
                                - HATEOAS nos endpoints de detalhes.

                                ### Ambiente

                                Projeto acadêmico em desenvolvimento.

                                Não utilizar dados pessoais reais.
                                """)
                        .contact(
                                new Contact()
                                        .name("Equipe FinGo")
                        )
                )
                .servers(List.of(
                        new Server()
                                .url("/")
                                .description(
                                        "Servidor atual da FinGo API"
                                )
                ))
                .tags(List.of(

                        tag(
                                "01 | Gestão de Usuários",
                                "Cadastro, consulta, atualização, exclusão "
                                        + "e conquistas dos usuários."
                        ),

                        tag(
                                "02 | Perfis Financeiros",
                                "Classificação financeira e vínculo "
                                        + "com usuários."
                        ),

                        tag(
                                "03 | Trilhas de Aprendizagem",
                                "Organização das jornadas de "
                                        + "educação financeira."
                        ),

                        tag(
                                "04 | Conteúdo Educacional",
                                "Gerenciamento das aulas "
                                        + "vinculadas às trilhas."
                        ),

                        tag(
                                "05 | Gamificação e Conquistas",
                                "Conquistas e pontuação da "
                                        + "jornada educacional."
                        ),

                        tag(
                                "06 | Monitoramento",
                                "Verificação do funcionamento da API."
                        )

                ));
    }

    // =========================================================
    // DOCUMENTAÇÃO V1
    // =========================================================

    @Bean
    public GroupedOpenApi fingoV1() {

        return GroupedOpenApi.builder()

                .group("v1")

                .displayName("FinGo API - V1")

                .pathsToMatch("/api/**")

                .addOpenApiMethodFilter(method ->
                        !method.getName().equals("consultarV2")
                )

                .addOpenApiCustomizer(
                        swaggerDocumentation.documentacaoFinGo()
                )

                .addOpenApiCustomizer(openApi -> {

                    openApi.getInfo().setVersion("1.0.0");

                    openApi.getInfo().setTitle(
                            "FinGo API - V1"
                    );

                    openApi.getInfo().setDescription(
                            documentacaoV1() + termosDeUso()
                    );
                })

                .addOpenApiCustomizer(this::configurarSeguranca)

                .build();
    }

    // =========================================================
    // DOCUMENTAÇÃO V2
    // =========================================================

    @Bean
    public GroupedOpenApi fingoV2() {

        return GroupedOpenApi.builder()

                .group("v2")

                .displayName("FinGo API - V2")

                .pathsToMatch("/api/**")

                .addOpenApiMethodFilter(method ->
                        !method.getName().equals("consultarV1")
                )

                .addOpenApiCustomizer(
                        swaggerDocumentation.documentacaoFinGo()
                )

                .addOpenApiCustomizer(openApi -> {

                    openApi.getInfo().setVersion("2.0.0");

                    openApi.getInfo().setTitle(
                            "FinGo API - V2"
                    );

                    openApi.getInfo().setDescription(
                            documentacaoV2() + termosDeUso()
                    );
                })

                .addOpenApiCustomizer(this::configurarSeguranca)

                .build();
    }

    // =========================================================
    // DESCRIÇÃO EXCLUSIVA DA V1
    // =========================================================

    private String documentacaoV1() {

        return """
                # FinGo API — Versão 1.0

                ## Visão geral

                A primeira versão da FinGo API disponibiliza
                os recursos fundamentais da plataforma
                de educação financeira gamificada.

                ## Funcionalidades principais

                ### 1. Gestão de usuários

                - Cadastro de usuários.
                - Consulta por identificador.
                - Atualização cadastral.
                - Exclusão de usuários.
                - Pesquisa personalizada.
                - Listagem paginada.

                ### 2. Perfis financeiros

                - Cadastro de perfis financeiros.
                - Classificação do perfil.
                - Associação entre perfil e usuário.

                ### 3. Trilhas de aprendizagem

                - Cadastro de trilhas.
                - Consulta de trilhas.
                - Atualização e exclusão.

                ### 4. Conteúdo educacional

                - Cadastro de aulas.
                - Associação de aulas às trilhas.
                - Consulta e gerenciamento de conteúdos.

                ### 5. Gamificação

                - Cadastro de conquistas.
                - Atribuição de conquistas aos usuários.
                - Consulta de conquistas.

                ## Recursos REST

                - Operações CRUD.
                - Validação de dados.
                - Paginação.
                - Consultas personalizadas.
                - Links HATEOAS.
                - Tratamento de erros HTTP.

                ## Versionamento da API

                Para consultar a representação V1 de um usuário:

                **Endpoint:**

                `GET /api/versoes/usuarios/{id}`

                **Cabeçalho obrigatório:**

                `X-API-Version: 1`

                **Exemplo de resposta:**

                ```json
                {
                  "id": 2,
                  "nome": "Usuario Exemplo",
                  "email": "usuario@example.com"
                }
                ```

                ## Códigos HTTP

                - **200:** operação realizada com sucesso.
                - **201:** recurso criado.
                - **204:** operação sem conteúdo de resposta.
                - **400:** dados inválidos.
                - **401:** autenticação ausente ou inválida.
                - **404:** recurso não encontrado.
                - **409:** conflito de dados.
                - **429:** limite de requisições excedido.
                - **500:** erro interno.

                ## Segurança

                Os endpoints protegidos utilizam API Key.

                Informe a chave pelo cabeçalho:

                `X-API-Key: SUA_CHAVE`

                A documentação V1 representa o contrato
                original da consulta versionada.

                """;
    }

    // =========================================================
    // DESCRIÇÃO EXCLUSIVA DA V2
    // =========================================================

    private String documentacaoV2() {

        return """
                # FinGo API — Versão 2.0

                ## Visão geral

                A versão 2 representa a evolução da FinGo API,
                mantendo os módulos de educação financeira
                e apresentando uma representação expandida
                dos dados do usuário.

                ## Evolução em relação à V1

                ### 1. Versionamento por cabeçalho

                A consulta versionada utiliza:

                `X-API-Version: 2`

                ### 2. Resposta expandida

                A representação V2 inclui:

                - Identificador do usuário.
                - Nome.
                - E-mail.
                - Versão da representação.
                - Total de conquistas.

                **Endpoint:**

                `GET /api/versoes/usuarios/{id}`

                **Exemplo de resposta:**

                ```json
                {
                  "id": 2,
                  "nome": "Usuario Exemplo",
                  "email": "usuario@example.com",
                  "versao": "2.0",
                  "totalConquistas": 3
                }
                ```

                ## Mecanismos de segurança da aplicação

                ### API Key

                A API utiliza autenticação por chave.

                Cabeçalho:

                `X-API-Key: SUA_CHAVE`

                No Swagger, utilize o botão **Authorize**.

                ### Rate Limiting — Token Bucket

                O controle de requisições utiliza
                um algoritmo Token Bucket em memória.

                Configuração inicial:

                - Capacidade de 30 tokens.
                - Reposição de 1 token a cada 2 segundos.
                - Resposta HTTP 429 quando o limite é excedido.

                ### Idempotência

                Requisições POST podem utilizar:

                `Idempotency-Key: IDENTIFICADOR_UNICO`

                Quando uma requisição válida é repetida
                com a mesma chave e o mesmo conteúdo,
                a aplicação pode reutilizar a resposta
                armazenada, evitando nova execução.

                ### CORS

                A aplicação configura origens autorizadas
                para integração com clientes web.

                ## Recursos compartilhados com a V1

                - CRUD das cinco entidades.
                - Paginação.
                - Validação de campos.
                - Consultas personalizadas.
                - Relacionamentos JPA.
                - HATEOAS.
                - Tratamento global de exceções.

                ## Códigos HTTP

                - **200:** operação realizada com sucesso.
                - **201:** recurso criado.
                - **204:** operação sem conteúdo.
                - **400:** dados inválidos.
                - **401:** API Key inválida.
                - **404:** recurso não encontrado.
                - **409:** conflito de dados.
                - **429:** limite excedido.
                - **500:** erro interno.

                ## Observações técnicas

                O Rate Limiting e a Idempotência utilizam
                armazenamento em memória nesta implementação
                acadêmica.

                Seus registros temporários não são
                preservados após reiniciar a aplicação.

                """;
    }

    // =========================================================
    // AUTENTICAÇÃO NO SWAGGER
    // =========================================================

    private void configurarSeguranca(OpenAPI openApi) {

        String esquema = "FinGoApiKey";

        if (openApi.getComponents() == null) {
            openApi.setComponents(new Components());
        }

        openApi.getComponents().addSecuritySchemes(
                esquema,
                new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("X-API-Key")
                        .description(
                                "Informe a API Key gerada pela FinGo."
                        )
        );

        if (openApi.getPaths() != null) {

            openApi.getPaths().forEach((path, item) -> {

                // A criação administrativa utiliza
                // X-Bootstrap-Token, não X-API-Key.
                if (path.equals("/api/admin/api-keys")
                        || path.equals("/api/status")) {
                    return;
                }

                item.readOperations().forEach(operation ->
                        operation.addSecurityItem(
                                new SecurityRequirement()
                                        .addList(esquema)
                        )
                );
            });
        }
    }

    // =========================================================
    // TERMOS DE USO
    // =========================================================

    private String termosDeUso() {

        return """

                ---

                # Termos de Uso da FinGo API

                **Última atualização: 13/11/2026**

                ## 1. Finalidade

                A FinGo API é uma aplicação acadêmica
                voltada à educação financeira gamificada.

                Sua finalidade é demonstrar recursos
                de desenvolvimento de APIs RESTful.

                ## 2. Utilização permitida

                A utilização é destinada a:

                - Atividades acadêmicas.
                - Avaliação educacional.
                - Testes autorizados.
                - Demonstração de funcionalidades.

                É proibida a utilização para atividades
                ilícitas ou tentativas de comprometer
                a disponibilidade do serviço.

                ## 3. Autenticação e credenciais

                Os endpoints protegidos exigem uma
                API Key válida.

                O consumidor da API é responsável
                pela proteção de suas credenciais.

                As chaves não devem ser divulgadas
                publicamente ou incluídas em
                repositórios de código.

                ## 4. Limites de utilização

                A FinGo poderá aplicar limites
                de requisições para proteger
                a disponibilidade do serviço.

                O excesso de requisições poderá
                resultar em HTTP 429.

                ## 5. Responsabilidades do usuário

                O consumidor deve:

                - Enviar dados válidos.
                - Respeitar os limites da API.
                - Proteger suas credenciais.
                - Evitar requisições maliciosas.
                - Utilizar o serviço apenas para
                  finalidades autorizadas.

                ## 6. Dados e privacidade

                O ambiente acadêmico deve utilizar
                exclusivamente dados fictícios
                ou devidamente autorizados.

                Não devem ser cadastradas senhas
                reais, documentos pessoais ou
                informações financeiras sensíveis.

                Os dados de teste poderão ser
                armazenados para funcionamento
                e avaliação da aplicação.

                ## 7. Conteúdo financeiro

                Os conteúdos da FinGo possuem
                finalidade exclusivamente educativa.

                Não constituem consultoria financeira,
                recomendação individualizada de
                investimentos ou garantia de resultados.

                ## 8. Disponibilidade

                A FinGo API encontra-se em
                desenvolvimento acadêmico.

                Não há garantia de disponibilidade
                contínua, desempenho ou ausência
                de interrupções.

                ## 9. Alterações

                As funcionalidades, endpoints,
                mecanismos de segurança e estes
                termos poderão ser atualizados
                durante a evolução do projeto.

                ## 10. Aceitação

                A utilização da API pressupõe
                conhecimento e observância das
                condições descritas nesta documentação.

                ---
                """;
    }

    // =========================================================
    // ORGANIZAÇÃO DAS TAGS
    // =========================================================

    private Tag tag(String nome, String descricao) {

        return new Tag()
                .name(nome)
                .description(descricao);
    }
}


