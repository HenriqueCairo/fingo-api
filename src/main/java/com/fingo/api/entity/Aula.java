
package com.fingo.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Schema(
        name = "Aula",
        description = """
        Representa uma unidade de aprendizagem da plataforma FinGo.

        Cada aula pertence a uma trilha de educação financeira
        e disponibiliza conteúdo educacional para o usuário.
        """
)
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador único da aula, gerado automaticamente.",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @NotBlank(message = "O título da aula é obrigatório.")
    @Size(
            min = 3,
            max = 150,
            message = "O título deve conter entre 3 e 150 caracteres."
    )
    @Column(nullable = false, length = 150)
    @Schema(
            description = "Título da aula apresentado na trilha de aprendizagem.",
            example = "Como organizar seu orçamento",
            minLength = 3,
            maxLength = 150,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String titulo;

    @NotBlank(message = "O conteúdo da aula é obrigatório.")
    @Size(
            min = 10,
            max = 5000,
            message = "O conteúdo deve conter entre 10 e 5000 caracteres."
    )
    @Column(nullable = false, length = 5000)
    @Schema(
            description = """
            Material educacional apresentado ao usuário.

            O conteúdo deve explicar conceitos de educação
            financeira de maneira clara e acessível.
            """,
            example = "Aprenda a identificar suas receitas, despesas fixas e variáveis para organizar seu orçamento mensal.",
            minLength = 10,
            maxLength = 5000,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String conteudo;

    @NotNull(message = "A aula deve estar vinculada a uma trilha.")
    @JsonIgnoreProperties("aulas")
    @ManyToOne
    @JoinColumn(name = "trilha_id", nullable = false)
    @Schema(
            description = """
            Trilha de aprendizagem à qual a aula pertence.

            Informe o ID de uma trilha previamente cadastrada.
            """,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Trilha trilha;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Trilha getTrilha() {
        return trilha;
    }

    public void setTrilha(Trilha trilha) {
        this.trilha = trilha;
    }
}

