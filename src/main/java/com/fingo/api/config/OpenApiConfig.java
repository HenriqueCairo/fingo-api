
package com.fingo.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fingoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FinGo API | Plataforma de Educação Financeira Gamificada")
                        .version("1.0.0")
                        .description("""
                    ## Bem-vindo à FinGo API

                    API RESTful para uma plataforma de educação
                    financeira gamificada, desenvolvida em Java 21
                    e Spring Boot.

                    ### Objetivo
                    Promover educação financeira por meio de
                    conteúdos organizados em trilhas, aulas,
                    perfis financeiros e conquistas.

                    ### Módulos
                    - **Usuários:** cadastro e gerenciamento.
                    - **Perfis financeiros:** classificação do usuário.
                    - **Trilhas:** jornadas de aprendizagem.
                    - **Aulas:** conteúdos educacionais.
                    - **Conquistas:** pontuação e gamificação.

                    ### Tecnologias
                    Java 21, Spring Boot, Spring Data JPA,
                    Hibernate, H2, Bean Validation,
                    Spring HATEOAS e OpenAPI 3.

                    ### Convenções REST
                    - GET: consulta de recursos.
                    - POST: criação de recursos.
                    - PUT: atualização de recursos.
                    - DELETE: exclusão de recursos.
                    - Listagens paginadas.
                    - Respostas JSON.
                    - Links HATEOAS nos endpoints de detalhes.

                    ### Ambiente
                    Versão acadêmica em desenvolvimento.
                    Não utilizar com dados pessoais reais.
                    """)
                        .contact(new Contact()
                                .name("Equipe FinGo")
                        )
                )
                .servers(List.of(
                        new Server()
                                .url("/")
                                .description("Servidor local de desenvolvimento")
                ))
                .tags(List.of(
                        tag("01 | Gestão de Usuários",
                                "Cadastro, consulta, atualização, exclusão e conquistas dos usuários."),
                        tag("02 | Perfis Financeiros",
                                "Classificação financeira e vínculo com usuários."),
                        tag("03 | Trilhas de Aprendizagem",
                                "Organização das jornadas de educação financeira."),
                        tag("04 | Conteúdo Educacional",
                                "Gerenciamento das aulas vinculadas às trilhas."),
                        tag("05 | Gamificação e Conquistas",
                                "Conquistas e pontuação da jornada educacional."),
                        tag("06 | Monitoramento",
                                "Verificação do funcionamento da API.")
                ));
    }

    private Tag tag(String nome, String descricao) {
        return new Tag()
                .name(nome)
                .description(descricao);
    }
}

