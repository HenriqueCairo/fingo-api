package com.fingo.api.repository;

import com.fingo.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Page<Usuario> findByNomeContainingIgnoreCase(
            String nome,
            Pageable pageable
    );
    @EntityGraph(attributePaths = "conquistas")
    Optional<Usuario> findWithConquistasById(Long id);
}
