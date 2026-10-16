
package com.fingo.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Schema(
        name = "Conquista",
        description = """
        Representa uma conquista da plataforma FinGo.

        As conquistas fazem parte do sistema de gamificação
        e recompensam os usuários por sua evolução na
        jornada de educação financeira.

        Cada conquista possui nome, descrição e uma
        quantidade de pontos.

        Uma mesma conquista pode ser atribuída a
        diferentes usuários.
        """
)
public class Conquista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador único da conquista, gerado automaticamente.",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @NotBlank(message = "O nome da conquista é obrigatório.")
    @Size(
            min = 3,
            max = 100,
            message = "O nome deve conter entre 3 e 100 caracteres."
    )
    @Column(length = 100, nullable = false)
    @Schema(
            description = "Nome da conquista exibido ao usuário.",
            example = "Primeira Conquista",
            minLength = 3,
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nome;

    @NotBlank(message = "A descrição da conquista é obrigatória.")
    @Size(
            min = 10,
            max = 500,
            message = "A descrição deve conter entre 10 e 500 caracteres."
    )
    @Column(length = 500, nullable = false)
    @Schema(
            description = """
            Explica o objetivo da conquista e a condição
            educacional que o usuário deve cumprir para recebê-la.
            """,
            example = "Concluiu sua primeira atividade de educação financeira.",
            minLength = 10,
            maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descricao;

    @NotNull(message = "A pontuação da conquista é obrigatória.")
    @PositiveOrZero(message = "A pontuação não pode ser negativa.")
    @Column(nullable = false)
    @Schema(
            description = """
            Quantidade de pontos concedidos ao usuário
            quando a conquista é atribuída.

            O valor deve ser um número inteiro maior
            ou igual a zero.
            """,
            example = "100",
            minimum = "0",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer pontos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getPontos() {
        return pontos;
    }

    public void setPontos(Integer pontos) {
        this.pontos = pontos;
    }
}

