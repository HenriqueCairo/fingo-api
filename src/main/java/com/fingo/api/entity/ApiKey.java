
package com.fingo.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_keys")
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String chaveHash;

    @Column(nullable = false, length = 100)
    private String descricao;

    @Column(nullable = false)
    private boolean ativa = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadaEm = LocalDateTime.now();

    public Long getId() { return id; }

    public String getChaveHash() { return chaveHash; }
    public void setChaveHash(String chaveHash) {
        this.chaveHash = chaveHash;
    }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public LocalDateTime getCriadaEm() { return criadaEm; }
}

