
package com.fingo.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuario")
@Schema(
        name = "Usuario",
        description = """
        Representa um usuário cadastrado na plataforma FinGo.

        O usuário pode possuir um perfil financeiro e receber
        conquistas durante sua jornada de educação financeira.

        O e-mail é único e a senha não é exibida nas respostas
        da API.
        """
)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador único do usuário, gerado automaticamente.",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @NotBlank(message = "O nome do usuário é obrigatório.")
    @Size(
            min = 2,
            max = 100,
            message = "O nome deve conter entre 2 e 100 caracteres."
    )
    @Schema(
            description = "Nome completo do usuário cadastrado no FinGo.",
            example = "Henrique Santana",
            minLength = 2,
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Informe um endereço de e-mail válido.")
    @Size(
            max = 150,
            message = "O e-mail deve conter no máximo 150 caracteres."
    )
    @Column(unique = true)
    @Schema(
            description = """
            Endereço de e-mail único utilizado para identificar
            o usuário na plataforma.
            """,
            example = "usuario@fingo.com",
            maxLength = 150,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(
            min = 8,
            message = "A senha deve conter pelo menos 8 caracteres."
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(
            description = """
            Senha informada no cadastro do usuário.

            Campo de entrada confidencial, omitido das
            respostas JSON da API.
            """,
            example = "SenhaForte123!",
            minLength = 8,
            accessMode = Schema.AccessMode.WRITE_ONLY,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String senha;

    @OneToOne(
            mappedBy = "usuario",
            cascade = CascadeType.ALL
    )
    @JsonIgnoreProperties("usuario")
    @Schema(
            description = """
            Perfil financeiro associado ao usuário.

            O perfil pode ser CONSERVADOR, MODERADO
            ou ARROJADO.
            """,
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private PerfilFinanceiro perfilFinanceiro;

    @ManyToMany
    @JoinTable(
            name = "usuario_conquista",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "conquista_id")
    )
    @Schema(
            description = """
            Conquistas recebidas pelo usuário durante
            sua jornada de aprendizagem.

            Cada conquista pode conceder pontos
            de gamificação.
            """,
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Set<Conquista> conquistas = new HashSet<>();

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public PerfilFinanceiro getPerfilFinanceiro() {
        return perfilFinanceiro;
    }

    public void setPerfilFinanceiro(PerfilFinanceiro perfilFinanceiro) {
        this.perfilFinanceiro = perfilFinanceiro;
    }

    public Set<Conquista> getConquistas() {
        return conquistas;
    }

    public void setConquistas(Set<Conquista> conquistas) {
        this.conquistas = conquistas;
    }
}
