package com.fingo.api.repository;

import com.fingo.api.entity.PerfilFinanceiro;
import com.fingo.api.entity.TipoPerfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;

public interface PerfilFinanceiroRepository
        extends JpaRepository<PerfilFinanceiro, Long> {

    Page<PerfilFinanceiro> findByTipo(
            TipoPerfil tipo,
            Pageable pageable
    );
    @EntityGraph(attributePaths = "usuario")
    Optional<PerfilFinanceiro> findWithUsuarioById(Long id);
}
