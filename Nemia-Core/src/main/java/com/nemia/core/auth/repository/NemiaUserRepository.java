package com.nemia.core.auth.repository;

import com.nemia.core.auth.model.NemiaUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NemiaUserRepository extends JpaRepository<NemiaUser, Long> {
    Optional<NemiaUser> findByUsername(String username);
}