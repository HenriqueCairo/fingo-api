
package com.fingo.api.repository;

import com.fingo.api.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    boolean existsByChaveHashAndAtivaTrue(String chaveHash);
}
