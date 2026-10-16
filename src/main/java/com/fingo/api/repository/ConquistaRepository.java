package com.fingo.api.repository;

import com.fingo.api.entity.Conquista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConquistaRepository extends JpaRepository<Conquista, Long> {

    Page<Conquista> findByPontosGreaterThanEqual(
            Integer pontos,
            Pageable pageable
    );
}
