package com.fingo.api.repository;

import com.fingo.api.entity.Trilha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;

public interface TrilhaRepository extends JpaRepository<Trilha, Long> {

    Page<Trilha> findByTituloContainingIgnoreCase(
            String titulo,
            Pageable pageable
    );
    @EntityGraph(attributePaths = "aulas")
    Optional<Trilha> findWithAulasById(Long id);
}
