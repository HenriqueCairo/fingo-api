package com.fingo.api.repository;

import com.fingo.api.entity.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AulaRepository extends JpaRepository<Aula, Long> {

    Page<Aula> findByTrilhaId(
            Long trilhaId,
            Pageable pageable
    );
}
