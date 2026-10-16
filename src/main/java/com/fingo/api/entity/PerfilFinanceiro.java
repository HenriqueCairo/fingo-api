
package com.fingo.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Schema(
        name = "PerfilFinanceiro",
        description = """
        Representa o perfil financeiro de um usuário da plataforma FinGo.

        O perfil classifica o usuário em CONSERVADOR, MODERADO
        ou ARROJADO, permitindo personalizar sua jornada
        de educação financeira.

        Cada perfil financeiro pode estar associado a um único
        usuário, por meio de um relacionamento One-to-One.
        """
)
public class PerfilFinanceiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador único do perfil financeiro, gerado automaticamente.",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @NotNull(message = "O tipo de perfil financeiro é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Schema(
            description = """
            Classificação do perfil financeiro do usuário.

            CONSERVADOR: prioriza segurança e estabilidade.
            MODERADO: busca equilíbrio entre segurança e risco.
            ARROJADO: aceita maior exposição a riscos.
            """,
            example = "MODERADO",
            allowableValues = {
                    "CONSERVADOR",
                    "MODERADO",
                    "ARROJADO"
            },
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private TipoPerfil tipo;

    @JsonIgnoreProperties({
            "perfilFinanceiro",
            "conquistas",
            "senha"
    })
    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    @Schema(
            description = """
            Usuário associado ao perfil financeiro.

            Para vincular o perfil, informe o ID de um usuário
            previamente cadastrado na plataforma FinGo.

            Um usuário pode possuir no máximo um perfil financeiro.
            """,
            example = "{\"id\": 1}"
    )
    private Usuario usuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoPerfil getTipo() {
        return tipo;
    }

    public void setTipo(TipoPerfil tipo) {
        this.tipo = tipo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}

