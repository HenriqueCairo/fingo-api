
package com.fingo.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Entity
@Schema(
        name = "Trilha",
        description = """
        Representa uma trilha de aprendizagem da plataforma FinGo.

        As trilhas organizam o conteúdo educacional em jornadas
        temáticas, permitindo que os usuários desenvolvam
        conhecimentos sobre finanças pessoais.

        Cada trilha pode conter diversas aulas, formando um
        relacionamento One-to-Many.
        """
)
public class Trilha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador único da trilha, gerado automaticamente.",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @NotBlank(message = "O título da trilha é obrigatório.")
    @Size(
            min = 3,
            max = 120,
            message = "O título deve conter entre 3 e 120 caracteres."
    )
    @Column(length = 120, nullable = false)
    @Schema(
            description = "Nome da trilha exibido no catálogo educacional.",
            example = "Educação Financeira Básica",
            minLength = 3,
            maxLength = 120,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String titulo;

    @NotBlank(message = "A descrição da trilha é obrigatória.")
    @Size(
            min = 10,
            max = 1000,
            message = "A descrição deve conter entre 10 e 1000 caracteres."
    )
    @Column(length = 1000, nullable = false)
    @Schema(
            description = """
            Apresentação dos objetivos educacionais da trilha.

            Deve explicar o que o usuário aprenderá durante
            sua jornada na plataforma FinGo.
            """,
            example = "Aprenda a organizar seu dinheiro, controlar seus gastos e construir hábitos financeiros saudáveis.",
            minLength = 10,
            maxLength = 1000,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descricao;

    @OneToMany(
            mappedBy = "trilha",
            cascade = CascadeType.ALL
    )
    @Schema(
            description = """
            Lista de aulas vinculadas à trilha.

            Uma trilha pode conter várias aulas.
            Cada aula pertence a uma trilha específica.
            """,
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private List<Aula> aulas = new ArrayList<>();

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<Aula> getAulas() {
        return aulas;
    }

    public void setAulas(List<Aula> aulas) {
        this.aulas = aulas;
    }
}

